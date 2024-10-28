package io.github.projectuniverse.uwbindoorpositioning.connections

import javax.inject.Inject

class ResponderConnectionManager @Inject constructor(
    private val responderNearbyConnector: ResponderNearbyConnector,
    private val responderUWBConnector: ResponderUWBConnector
) {
    // UWB ranging state
    val distanceState = responderUWBConnector.distanceState
    val azimuthState = responderUWBConnector.azimuthState
    val elevationState = responderUWBConnector.elevationState

    // Anchor state
    val anchorLatitudeState = responderNearbyConnector.anchorLatitudeState
    val anchorLongitudeState = responderNearbyConnector.anchorLongitudeState
    val anchorCompassBearingState = responderNearbyConnector.anchorCompassBearingState

    // Initializes UWB session and gets ResponderUWBSessionData needed to start ranging
    suspend fun initializeUWBSession(): ResponderUWBSessionData? {
        return responderUWBConnector.initializeUWBSession()
    }

    // Starts the search for nearby devices
    fun startDiscovery(
        responderUWBSessionData: ResponderUWBSessionData,
        onNearbyConnectionEstablished: (AnchorUWBSessionData) -> Unit
    ) {
        responderNearbyConnector.startDiscovery(
            responderUWBSessionData = responderUWBSessionData,
            onNearbyConnectionEstablished = onNearbyConnectionEstablished
        )
    }

    // Starts UWB ranging
    fun startRanging(
        anchorUWBSessionData: AnchorUWBSessionData,
        onUWBConnectionLost: () -> Unit
    ) {
        responderUWBConnector.startRanging(
            anchorUWBSessionData = anchorUWBSessionData,
            onUWBConnectionLost = onUWBConnectionLost
        )
    }

    // Ends all established Nearby Connections and UWB connections, as well as the search for nearby devices
    fun endAllConnections() {
        responderNearbyConnector.endNearbyConnections()
        responderUWBConnector.endUWBConnections()
    }
}