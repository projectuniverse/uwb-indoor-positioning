package com.example.uwbindoorpositioning

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import androidx.core.uwb.UwbManager
import androidx.core.uwb.exceptions.UwbServiceNotAvailableException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // Preferences are accessible as a flow
    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    val permissionsToRequest = mutableListOf(
        // Permissions required by Nearby Connections
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        // Permission required by UWB API
        Manifest.permission.UWB_RANGING
    ).apply {
        // Additional permission needed by Nearby Connections for API >= 33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addAll(arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES))
        }
    }.toTypedArray()
    private val uwbManager = UwbManager.createInstance(context)

    // State variables
    private val _isDeviceUWBCapableState = MutableStateFlow(isDeviceUWBCapable())
    private val _arePermissionsGrantedState = MutableStateFlow(arePermissionsGranted())
    private val _doesDeviceSupportUWBRangingState = MutableStateFlow<Boolean?>(null)
    private val _isUWBAvailableState = MutableStateFlow<Boolean?>(null)
    val isDeviceUWBCapableState = _isDeviceUWBCapableState.asStateFlow()
    val arePermissionsGrantedState = _arePermissionsGrantedState.asStateFlow()
    val doesDeviceSupportUWBRangingState = _doesDeviceSupportUWBRangingState.asStateFlow()
    val isUWBAvailableState = _isUWBAvailableState.asStateFlow()

    init {
        viewModelScope.launch {
            if (isDeviceUWBCapableState.value) {
                // Check if UWB is still available every 1 second
                while (isActive) {
                    val isUWBAvailable = isUWBAvailable()
                    _isUWBAvailableState.value = isUWBAvailable
                    if (doesDeviceSupportUWBRangingState.value == null && isUWBAvailable)  {
                        _doesDeviceSupportUWBRangingState.value = doesDeviceSupportUWBRanging()
                    }
                    delay(1000.milliseconds)
                }
            }
            else {
                _doesDeviceSupportUWBRangingState.value = false
                _isUWBAvailableState.value = false
            }
        }
    }

    private fun isDeviceUWBCapable(): Boolean {
        return context.packageManager.hasSystemFeature("android.hardware.uwb")
    }

    /*
     * Some devices support UWB but not UWB ranging. This functions returns true in that case, otherwise false.
     * The function returns null if UWB is unavailable and an answer cannot be determined.
     */
    private suspend fun doesDeviceSupportUWBRanging() : Boolean? {
        try {
            val controllerSessionScope = uwbManager.controllerSessionScope()
            val isDistanceSupported = controllerSessionScope.rangingCapabilities.isDistanceSupported
            val isAzimuthalAngleSupported =
                controllerSessionScope.rangingCapabilities.isAzimuthalAngleSupported
            val isElevationAngleSupported =
                controllerSessionScope.rangingCapabilities.isElevationAngleSupported
            return isDistanceSupported && isAzimuthalAngleSupported && isElevationAngleSupported
        } catch (exception: UwbServiceNotAvailableException) {
            return null
        }
    }

    // Returns false if UWB is turned off or phone is in airplane mode
    private suspend fun isUWBAvailable() : Boolean {
        return uwbManager.isAvailable()
    }

    // Call function in new coroutine since setAppTheme is a suspend function
    fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.setAppTheme(appTheme)
        }
    }

    fun onPermissionResult() {
        _arePermissionsGrantedState.value = arePermissionsGranted()
    }

    private fun arePermissionsGranted(): Boolean {
        for (permission in permissionsToRequest) {
            if (checkCallingOrSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}