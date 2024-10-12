package com.example.uwbindoorpositioning

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context,
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
    private val _arePermissionsGrantedState = MutableStateFlow(arePermissionsGranted())
    val arePermissionsGrantedState = _arePermissionsGrantedState.asStateFlow()

    fun isDeviceUWBCapable(): Boolean {
        return context.packageManager.hasSystemFeature("android.hardware.uwb")
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