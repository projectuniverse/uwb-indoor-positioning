package io.github.projectuniverse.uwbindoorpositioning.ui.screens.anchor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.projectuniverse.uwbindoorpositioning.connections.AnchorConnectionManager
import io.github.projectuniverse.uwbindoorpositioning.connections.AnchorNearbyPayload
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = AnchorSearchViewModel.Factory::class)
class AnchorSearchViewModel @AssistedInject constructor(
    @Assisted("anchorLatitude") val anchorLatitude: String,
    @Assisted("anchorLongitude") val anchorLongitude: String,
    @Assisted("anchorCompassBearing") val anchorCompassBearing: String,
    private val anchorConnectionManager: AnchorConnectionManager,
    @ApplicationContext val context: Context
) : ViewModel() {
    // Needed in order to pass manual arguments to ViewModel
    @AssistedFactory interface Factory {
        fun create(
            @Assisted("anchorLatitude") anchorLatitude: String,
            @Assisted("anchorLongitude") anchorLongitude: String,
            @Assisted("anchorCompassBearing") anchorCompassBearing: String
        ): AnchorSearchViewModel
    }

    init {
        viewModelScope.launch {
            // Initial start of advertising
            startAdvertising()
        }
    }

    /*
     * This functions initializes a UWB session by getting the relevant anchor UWB session data.
     * It then starts searching for nearby devices and starts ranging if a responder is found.
     * When ranging has started, the function calls itself in order to start searching
     * for other responders and establish multiple UWB sessions.
     */
    private fun startAdvertising() {
        viewModelScope.launch {
            var anchorUWBSessionData = anchorConnectionManager.initializeUWBSession()
            while (isActive && anchorUWBSessionData == null) {
                anchorUWBSessionData = anchorConnectionManager.initializeUWBSession()
                delay(1000.milliseconds)
            }
            if (anchorUWBSessionData != null) {
                val anchorNearbyPayload = AnchorNearbyPayload(
                    anchorUWBSessionData = anchorUWBSessionData,
                    anchorLatitude = anchorLatitude.toDouble(),
                    anchorLongitude = anchorLongitude.toDouble(),
                    anchorCompassBearing = anchorCompassBearing.toInt()
                )
                anchorConnectionManager.startAdvertising(
                    anchorNearbyPayload = anchorNearbyPayload,
                    onNearbyConnectionEstablished = { responderNearbyPayload ->
                        anchorConnectionManager.startRanging(
                            responderNearbyPayload = responderNearbyPayload
                        )
                        startAdvertising()
                    }
                )
            }
        }
    }

    // Called when ViewModel gets destroyed
    override fun onCleared() {
        anchorConnectionManager.endAllConnections()
        super.onCleared()
    }
}