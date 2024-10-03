package com.example.uwbindoorpositioning.connections

import kotlinx.serialization.Serializable

/*
 * Data class that holds the anchor's relevant information for establishing
 * a UWB connection and to start ranging.
 */
@Serializable
data class AnchorUWBSessionData(
    val anchorLocalAddress: ByteArray,
    val sessionId: Int,
    val sessionKeyInfo: ByteArray,
    val channel: Int,
    val preambleIndex: Int,
)
