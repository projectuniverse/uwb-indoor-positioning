package io.github.projectuniverse.uwbindoorpositioning.connections

import kotlinx.serialization.Serializable

/*
 * Data class that holds the information sent to the anchor in order to establish
 * a UWB connection and start ranging.
 */
@Serializable
data class ResponderNearbyPayload(
    val responderUWBSessionData: ResponderUWBSessionData,
    val anchorUWBSessionData: AnchorUWBSessionData
)