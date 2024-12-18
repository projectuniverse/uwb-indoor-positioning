package io.github.projectuniverse.uwbindoorpositioning.ui.screens.responder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.projectuniverse.uwbindoorpositioning.connections.ResponderConnectionManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ResponderViewModel @Inject constructor(
    private val responderConnectionManager: ResponderConnectionManager,
    @ApplicationContext val context: Context
) : ViewModel() {
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
            // Initial start of discovery
            startDiscovery()
        }
    }

    /*
     * This functions initializes a UWB session by getting the relevant responder UWB session data.
     * It then starts searching for nearby devices and starts ranging if an anchor is found.
     * When ranging has started, the function will not start searching for other anchors until
     * the UWB connection is lost. This ensures that the responder is only connected to one anchor
     * at a time.
     */
    private fun startDiscovery() {
        viewModelScope.launch {
            var responderUWBSessionData = responderConnectionManager.initializeUWBSession()
            while (isActive && responderUWBSessionData == null) {
                responderUWBSessionData = responderConnectionManager.initializeUWBSession()
                delay(1000.milliseconds)
            }
            if (responderUWBSessionData != null) {
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
    }

    // This function calculates the responder's precise location relative to the anchor
    fun getPreciseLocation(
        distance: Float,
        azimuth: Float,
        anchorLatitude: Double,
        anchorLongitude: Double,
        anchorCompassBearing: Int
    ): List<String> {
        val anchorLocation = LatLng(anchorLatitude, anchorLongitude)
        // Find heading (assuming negative azimuth is right of device and positive azimuth left of it)
        val heading = (180.0 + azimuth + anchorCompassBearing) % 360
        val preciseLocation = SphericalUtil.computeOffset(
            anchorLocation,
            distance.toDouble(),
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
    * Note: the last two parameters are especially relevant for the azimuth. Even though Google claims the
    * azimuth is in the range [-90,90], the responder sometimes receives values outside of that range.
    * It is also relevant when no min or max value is given, e.g., when formatting the distance.
    */
    fun formatDecimalNumber(
        decimalNumber: Double,
        numberOfDecimalPlaces: Int,
        minValue: Double = Double.NEGATIVE_INFINITY,
        maxValue: Double = Double.POSITIVE_INFINITY
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