package com.example.uwbindoorpositioning.connections

import android.content.Context
import androidx.core.uwb.*
import androidx.core.uwb.RangingResult.RangingResultPeerDisconnected
import androidx.core.uwb.RangingResult.RangingResultPosition
import com.google.android.gms.nearby.uwb.RangingParameters.SUB_SESSION_ID_UNSET
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class AnchorUWBConnector @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val uwbManager = UwbManager.createInstance(context)

    // The jobs that handle the uwb ranging
    private val jobs = mutableListOf<Job>()

    // Controller session scopes for the current uwb sessions
    private val controllerSessionScopes = mutableMapOf<String, UwbControllerSessionScope>()

    suspend fun doesDeviceSupportUWBRanging() : Boolean {
        val controllerSessionScope = uwbManager.controllerSessionScope()
        val isDistanceSupported = controllerSessionScope.rangingCapabilities.isDistanceSupported
        val isAzimuthalAngleSupported = controllerSessionScope.rangingCapabilities.isAzimuthalAngleSupported
        val isElevationAngleSupported = controllerSessionScope.rangingCapabilities.isElevationAngleSupported
        return isDistanceSupported && isAzimuthalAngleSupported && isElevationAngleSupported
    }

    /*
     * Initializes a new UWB session and returns the anchor's relevant UWB session data.
     * This function needs to be called before each new UWB session.
     * Especially, this function needs to be called before calling startRanging.
     */
    suspend fun initializeUWBSession(): AnchorUWBSessionData {
        // Note: Android allocates a new random local address for every new session
        val controllerSessionScope = uwbManager.controllerSessionScope()
        val anchorLocalAddress = controllerSessionScope.localAddress.address
        val sessionId = Random.nextInt()
        val sessionKeyInfo = Random.nextBytes(8)
        val channel = controllerSessionScope.uwbComplexChannel.channel
        val preambleIndex = controllerSessionScope.uwbComplexChannel.preambleIndex
        val anchorUWBSessionData = AnchorUWBSessionData(
            anchorLocalAddress = anchorLocalAddress,
            sessionId = sessionId,
            sessionKeyInfo = sessionKeyInfo,
            channel = channel,
            preambleIndex = preambleIndex
        )
        controllerSessionScopes[anchorLocalAddress.contentToString()] = controllerSessionScope
        return anchorUWBSessionData
    }

    fun startRanging(
        responderNearbyPayload: ResponderNearbyPayload
    ) {
        val responderLocalAddress = responderNearbyPayload.responderUWBSessionData.responderLocalAddress
        val anchorLocalAddress = responderNearbyPayload.anchorUWBSessionData.anchorLocalAddress
        val sessionId = responderNearbyPayload.anchorUWBSessionData.sessionId
        val sessionKeyInfo = responderNearbyPayload.anchorUWBSessionData.sessionKeyInfo
        val sessionScope = controllerSessionScopes[anchorLocalAddress.contentToString()]!!
        val complexChannel = sessionScope.uwbComplexChannel
        val peerDevices = listOf(UwbDevice(UwbAddress(responderLocalAddress)))
        // Encryption is static STS
        val rangingParameters = RangingParameters(
            uwbConfigType = RangingParameters.CONFIG_MULTICAST_DS_TWR,
            sessionId = sessionId,
            subSessionId = SUB_SESSION_ID_UNSET,
            // SessionKeyInfo is used to encrypt the ranging session
            sessionKeyInfo = sessionKeyInfo,
            subSessionKeyInfo = null,
            complexChannel = complexChannel,
            peerDevices = peerDevices,
            updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT
        )
        val sessionFlow = sessionScope.prepareSession(rangingParameters)
        jobs.add(
            CoroutineScope(Dispatchers.Main.immediate).launch {
                sessionFlow.collect {
                    when (it) {
                        is RangingResultPosition -> {}
                        is RangingResultPeerDisconnected -> {}
                    }
                }
            }
        )
    }

    /*
    * Ends all established connections with uwb devices for cleanup.
    * This need to be called, because the viewmodel scope is different
    * from this coroutine scope, so destroying the viewmodel would not
    * destroy these coroutines and ranging would continue.
    */
    fun endAllUWBConnections() {
        jobs.forEach { entry ->
            entry.cancel()
        }
    }
}