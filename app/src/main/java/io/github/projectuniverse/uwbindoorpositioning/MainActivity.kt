package io.github.projectuniverse.uwbindoorpositioning

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
import dagger.hilt.android.AndroidEntryPoint
import io.github.projectuniverse.uwbindoorpositioning.data.AppTheme
import io.github.projectuniverse.uwbindoorpositioning.ui.UWBIndoorPositioningApp
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.UWBIndoorPositioningTheme

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
            val isLocationOnState =
                viewModel.isLocationTurnedOnState.collectAsStateWithLifecycle()
            val arePermissionsGrantedState =
                viewModel.arePermissionsGrantedState.collectAsStateWithLifecycle()
            val doesDeviceSupportUWBRangingState =
                viewModel.doesDeviceSupportUWBRangingState.collectAsStateWithLifecycle()
            val isUWBAvailableState = viewModel.isUWBAvailableState.collectAsStateWithLifecycle()

            // State variables
            val userPreferences = userPreferencesState.value
            val isDeviceUWBCapable = isDeviceUWBCapableState.value
            val isLocationTurnedOn = isLocationOnState.value
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
                    /*
                     * Only request permissions (again) if the device is UWB-capable, the required
                     * permissions are not granted and the location is turned on. The latter is important,
                     * because Android might say that location permissions are not granted if the user
                     * turned off their location, even if the location permissions are granted.
                     */
                    if (isDeviceUWBCapable && !arePermissionsGranted && isLocationTurnedOn) {
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
                                    if (event == Lifecycle.Event.ON_START) {
                                        multiplePermissionResultLauncher.launch(viewModel.permissionsToRequest)
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)
                                onDispose {
                                    lifecycleOwner.lifecycle.removeObserver(observer)
                                }
                            }
                        )
                    }

                    UWBIndoorPositioningApp(
                        isDeviceUWBCapable = isDeviceUWBCapable,
                        isLocationTurnedOn = isLocationTurnedOn,
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