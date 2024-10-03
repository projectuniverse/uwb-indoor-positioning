package com.example.uwbindoorpositioning.connections

import kotlinx.serialization.Serializable

/*
 * Data class that holds the information sent to the responder in order to establish
 * a UWB connection and start ranging. Also includes the anchor's location and compass bearing.
 */
@Serializable
data class AnchorNearbyPayload(
    val anchorUWBSessionData: AnchorUWBSessionData,
    val anchorLatitude: Double,
    val anchorLongitude: Double,
    val anchorCompassBearing: Int
)

