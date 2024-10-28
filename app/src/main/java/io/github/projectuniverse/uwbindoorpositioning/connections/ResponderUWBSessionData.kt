package io.github.projectuniverse.uwbindoorpositioning.connections

import kotlinx.serialization.Serializable

/*
 * Data class that holds the responder's relevant information for establishing
 * a UWB connection and to start ranging.
 */
@Serializable
data class ResponderUWBSessionData(
    val responderLocalAddress: ByteArray
)