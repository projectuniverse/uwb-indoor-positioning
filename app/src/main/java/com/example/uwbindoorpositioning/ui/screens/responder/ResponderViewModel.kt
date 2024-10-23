package com.example.uwbindoorpositioning.ui.screens.responder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uwbindoorpositioning.connections.ResponderConnectionManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos

@HiltViewModel
class ResponderViewModel @Inject constructor(
    private val responderConnectionManager: ResponderConnectionManager,
    @ApplicationContext val context: Context
) : ViewModel() {
    // State about device's own hardware capabilities
    private val _doesDeviceSupportUWBRangingState = MutableStateFlow<Boolean?>(null)
    val doesDeviceSupportUWBRangingState = _doesDeviceSupportUWBRangingState.asStateFlow()

    // UWB ranging state
    val distanceState = responderConnectionManager.distanceState
    val azimuthState = responderConnectionManager.azimuthState
    val elevationState = responderConnectionManager.elevationState

    // Anchor state
    val anchorLatitudeState = responderConnectionManager.anchorLatitudeState
    val anchorLongitudeState = responderConnectionManager.anchorLongitudeState
    val anchorCompassBearingState = responderConnectionManager.anchorCompassBearingState

    init {
        viewModelScope.launch {
            val doesDeviceSupportUWBRanging = responderConnectionManager.doesDeviceSupportUWBRanging()
            _doesDeviceSupportUWBRangingState.value = doesDeviceSupportUWBRanging
            if (doesDeviceSupportUWBRanging) {
                // Initial start of discovery
                startDiscovery()
            }
        }
    }

    private fun startDiscovery() {
        viewModelScope.launch {
            val responderUWBSessionData = responderConnectionManager.initializeUWBSession()
            responderConnectionManager.startDiscovery(
                responderUWBSessionData = responderUWBSessionData,
                onNearbyConnectionEstablished = { anchorUWBSessionData ->
                    responderConnectionManager.startRanging(
                        anchorUWBSessionData = anchorUWBSessionData,
                        onUWBConnectionLost = {
                            startDiscovery()
                        }
                    )
                }
            )
        }
    }

    fun getPreciseLocation(
        distance: Float,
        azimuth: Float,
        elevation: Float,
        anchorLatitude: Double,
        anchorLongitude: Double,
        anchorCompassBearing: Int
    ): List<String> {
        val anchorLocation = LatLng(anchorLatitude, anchorLongitude)
        /*
         * Trigonometry to find horizontal distance and heading (assuming negative azimuth
         * is right of device and positive azimuth left of it)
         */
        val horizontalDistance = cos(abs(elevation) * (PI/180)) * distance
        val heading = (180.0 + azimuth + anchorCompassBearing) % 360
        val preciseLocation = SphericalUtil.computeOffset(
            anchorLocation,
            horizontalDistance,
            heading
        )
        val formattedPreciseLatitude = formatDecimalNumber(
            decimalNumber = preciseLocation.latitude,
            numberOfDecimalPlaces = 7
        )
        val formattedPreciseLongitude = formatDecimalNumber(
            decimalNumber = preciseLocation.longitude,
            numberOfDecimalPlaces = 7
        )
        val formattedPreciseLocation = listOf(formattedPreciseLatitude, formattedPreciseLongitude)
        return formattedPreciseLocation
    }

    /*
    * This function formats decimal numbers and:
    * - limits the decimalNumber's number of decimal places to numberOfDecimalPlaces
    * - fills up the fractional part with zeros if the decimalNumber doesn't have numberOfDecimalPlaces many decimal places
    * - removes a minus symbol if the number before the decimal point is a zero
    * - sets the return value to minValue if decimalNumber < minValue
    * - sets the return value to maxValue if value > decimalNumber
    *
    * Note: the last two points are relevant for the azimuth. Even though Google claims the
    * azimuth is in the range [-90,90], the responder sometimes receives values outside of that range.
    */
    fun formatDecimalNumber(
        decimalNumber: Double,
        numberOfDecimalPlaces: Int,
        minValue: Double = Double.MIN_VALUE,
        maxValue: Double = Double.MAX_VALUE
    ): String {
        if (numberOfDecimalPlaces < 1 || minValue > maxValue) {
            return ""
        }
        val unformattedOutputValue =
            if (decimalNumber < minValue) {
                minValue.toString()
            } else if (decimalNumber > maxValue) {
                maxValue.toString()
            } else {
                decimalNumber.toString()
            }
        // Part before the decimal point
        val wholeNumberWithoutLeadingZeros =
            unformattedOutputValue
                .substring(0, unformattedOutputValue.indexOf('.'))
                .toInt()
                .toString()
        // Part after the decimal point
        var fractionalPart =
            unformattedOutputValue
                .substring(unformattedOutputValue.indexOf('.') + 1)
        if (fractionalPart.length < numberOfDecimalPlaces) {
            fractionalPart += "0".repeat(numberOfDecimalPlaces - fractionalPart.length)
        }
        else if (fractionalPart.length > numberOfDecimalPlaces) {
            fractionalPart = fractionalPart.substring(0, numberOfDecimalPlaces)
        }
        return "$wholeNumberWithoutLeadingZeros.$fractionalPart"
    }

    // Called when ViewModel gets destroyed
    override fun onCleared() {
        responderConnectionManager.endAllConnections()
        super.onCleared()
    }
}