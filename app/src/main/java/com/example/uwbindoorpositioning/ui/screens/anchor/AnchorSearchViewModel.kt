package com.example.uwbindoorpositioning.ui.screens.anchor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uwbindoorpositioning.connections.AnchorConnectionManager
import com.example.uwbindoorpositioning.connections.AnchorNearbyPayload
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO check which hilt injected fields can be private (check in all classes)
@HiltViewModel(assistedFactory = AnchorSearchViewModel.Factory::class)
class AnchorSearchViewModel @AssistedInject constructor(
    @Assisted("anchorLatitude") val anchorLatitude: String,
    @Assisted("anchorLongitude") val anchorLongitude: String,
    @Assisted("anchorCompassBearing") val anchorCompassBearing: String,
    private val anchorConnectionManager: AnchorConnectionManager,
    @ApplicationContext val context: Context
) : ViewModel() {
    // Needed to pass manual arguments to ViewModel
    @AssistedFactory interface Factory {
        fun create(
            @Assisted("anchorLatitude") anchorLatitude: String,
            @Assisted("anchorLongitude") anchorLongitude: String,
            @Assisted("anchorCompassBearing") anchorCompassBearing: String
        ): AnchorSearchViewModel
    }

    // State about device's own hardware capabilities
    private val _doesDeviceSupportUWBRangingState = MutableStateFlow<Boolean?>(null)
    val doesDeviceSupportUWBRangingState = _doesDeviceSupportUWBRangingState.asStateFlow()

    init {
        viewModelScope.launch {
            val doesDeviceSupportUWBRanging = anchorConnectionManager.doesDeviceSupportUWBRanging()
            _doesDeviceSupportUWBRangingState.value = doesDeviceSupportUWBRanging
            if (doesDeviceSupportUWBRanging) {
                // Initial start of advertising
                startAdvertising()
            }
        }
    }

    private fun startAdvertising() {
        viewModelScope.launch {
            val anchorUWBSessionData = anchorConnectionManager.initializeUWBSession()
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

    // Called when ViewModel gets destroyed
    override fun onCleared() {
        anchorConnectionManager.endAllConnections()
        super.onCleared()
    }
}