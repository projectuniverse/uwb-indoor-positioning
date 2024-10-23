package com.example.uwbindoorpositioning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.ui.UWBIndoorPositioningApp
import com.example.uwbindoorpositioning.ui.theme.UWBIndoorPositioningTheme
import dagger.hilt.android.AndroidEntryPoint

/*
 * @AndroidEntryPoint makes Hilt create a dependencies container which is
 * attached to MainActivity's lifecycle
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels() // Injected by Hilt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // State
            val userPreferencesState =
                viewModel.userPreferencesFlow.collectAsStateWithLifecycle(initialValue = null)
            val isDeviceUWBCapableState =
                viewModel.isDeviceUWBCapableState.collectAsStateWithLifecycle()
            val arePermissionsGrantedState =
                viewModel.arePermissionsGrantedState.collectAsStateWithLifecycle()
            val doesDeviceSupportUWBRangingState =
                viewModel.doesDeviceSupportUWBRangingState.collectAsStateWithLifecycle()
            val isUWBAvailableState = viewModel.isUWBAvailableState.collectAsStateWithLifecycle()

            // State variables
            val userPreferences = userPreferencesState.value
            val isDeviceUWBCapable = isDeviceUWBCapableState.value
            val arePermissionsGranted = arePermissionsGrantedState.value
            val doesDeviceSupportUWBRanging = doesDeviceSupportUWBRangingState.value
            val isUWBAvailable = isUWBAvailableState.value

            // Checking for null makes sure the appTheme is known before displaying the app
            if (userPreferences != null) {
                val isDark = when (userPreferences.appTheme) {
                    AppTheme.MODE_AUTO -> isSystemInDarkTheme()
                    AppTheme.MODE_DAY -> false
                    AppTheme.MODE_NIGHT -> true
                }

                UWBIndoorPositioningTheme(darkTheme = isDark) {
                    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions(),
                        onResult = {
                            viewModel.permissionsToRequest.forEach { _ ->
                                viewModel.onPermissionResult()
                            }
                        }
                    )
                    val lifecycleOwner = LocalLifecycleOwner.current

                    DisposableEffect(
                        key1 = lifecycleOwner,
                        effect = {
                            val observer = LifecycleEventObserver { _, event ->
                                // Only request (again) if permissions are not granted and device is UWB capable
                                if (event == Lifecycle.Event.ON_START && !arePermissionsGranted && isDeviceUWBCapable) {
                                    multiplePermissionResultLauncher.launch(viewModel.permissionsToRequest)
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(observer)
                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(observer)
                            }
                        }
                    )

                    UWBIndoorPositioningApp(
                        isDeviceUWBCapable = isDeviceUWBCapable,
                        arePermissionsGranted = arePermissionsGranted,
                        doesDeviceSupportUWBRanging = doesDeviceSupportUWBRanging,
                        isUWBAvailable = isUWBAvailable,
                        selectedTheme = userPreferences.appTheme,
                        setAppTheme = { appTheme -> viewModel.setAppTheme(appTheme) }
                    )
                }
            }
        }
    }
}