package com.example.uwbindoorpositioning.connections

import javax.inject.Inject

class AnchorConnectionManager @Inject constructor(
    private val anchorNearbyConnector: AnchorNearbyConnector,
    private val anchorUWBConnector: AnchorUWBConnector
) {
    suspend fun doesDeviceSupportUWBRanging() : Boolean {
        return anchorUWBConnector.doesDeviceSupportUWBRanging()
    }

    suspend fun initializeUWBSession(): AnchorUWBSessionData {
        return anchorUWBConnector.initializeUWBSession()
    }

    // Starts the search for nearby devices
    fun startAdvertising(
        anchorNearbyPayload: AnchorNearbyPayload,
        onNearbyConnectionEstablished: (ResponderNearbyPayload) -> Unit
    ) {
        anchorNearbyConnector.startAdvertising(
            anchorNearbyPayload = anchorNearbyPayload,
            onNearbyConnectionEstablished = onNearbyConnectionEstablished
        )
    }

    // Starts UWB ranging
    fun startRanging(
        responderNearbyPayload: ResponderNearbyPayload
    ) {
        anchorUWBConnector.startRanging(
            responderNearbyPayload = responderNearbyPayload
        )
    }

    // Ends all established Nearby Connections and UWB connections, as well as the search for nearby devices
    fun endAllConnections() {
        anchorNearbyConnector.endAllNearbyConnections()
        anchorUWBConnector.endAllUWBConnections()
    }
}