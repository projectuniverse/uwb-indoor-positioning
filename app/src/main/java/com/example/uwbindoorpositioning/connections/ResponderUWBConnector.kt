package com.example.uwbindoorpositioning.connections

import android.content.Context
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingResult.RangingResultPeerDisconnected
import androidx.core.uwb.RangingResult.RangingResultPosition
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbControleeSessionScope
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import androidx.core.uwb.exceptions.UwbServiceNotAvailableException
import com.google.android.gms.nearby.uwb.RangingParameters.SUB_SESSION_ID_UNSET
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResponderUWBConnector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val uwbManager = UwbManager.createInstance(context)

    // The job that handles the uwb ranging
    private var job: Job? = null

    // Controlee session scope for the current uwb session
    private var controleeSessionScope: UwbControleeSessionScope? = null

    // UWB ranging state
    private val _distanceState = MutableStateFlow<Float?>(null)
    private val _azimuthState = MutableStateFlow<Float?>(null)
    private val _elevationState = MutableStateFlow<Float?>(null)
    val distanceState = _distanceState.asStateFlow()
    val azimuthState = _azimuthState.asStateFlow()
    val elevationState = _elevationState.asStateFlow()

    /*
     * Initializes a new UWB session and returns the responder's relevant UWB session data.
     * This function needs to be called before each new UWB session.
     * Especially, this function needs to be called before calling startRanging.
     * If UWB is unavailable, this function returns null.
     */
    suspend fun initializeUWBSession(): ResponderUWBSessionData? {
        // Note: Android allocates a new random local address for every new session
        try {
            val controleeSessionScope = uwbManager.controleeSessionScope()
            val responderLocalAddress = controleeSessionScope.localAddress.address
            val responderUWBSessionData = ResponderUWBSessionData(
                responderLocalAddress = responderLocalAddress
            )
            this.controleeSessionScope = controleeSessionScope
            return responderUWBSessionData
        } catch (exception: UwbServiceNotAvailableException) {
            return null
        }
    }

    // Starts a new ranging session with the given anchor
    fun startRanging(
        anchorUWBSessionData: AnchorUWBSessionData,
        onUWBConnectionLost: () -> Unit
    ) {
        val anchorLocalAddress = anchorUWBSessionData.anchorLocalAddress
        val sessionId = anchorUWBSessionData.sessionId
        val sessionKeyInfo = anchorUWBSessionData.sessionKeyInfo
        val channel = anchorUWBSessionData.channel
        val preambleIndex = anchorUWBSessionData.preambleIndex
        val complexChannel =
            UwbComplexChannel(
                channel,
                preambleIndex
            )
        val peerDevices = listOf(UwbDevice(UwbAddress(anchorLocalAddress)))
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
        val sessionFlow = controleeSessionScope!!.prepareSession(rangingParameters)
        val currentJob = CoroutineScope(Dispatchers.Main.immediate).launch {
            sessionFlow.collect {
                when(it) {
                    is RangingResultPosition -> {
                        // Note: either value could be null at any time
                        val newDistance = it.position.distance?.value
                        val newAzimuth = it.position.azimuth?.value
                        val newElevation = it.position.elevation?.value
                        // Only assign new value if not null
                        _distanceState.value = newDistance ?: _distanceState.value
                        _azimuthState.value = newAzimuth ?: _azimuthState.value
                        _elevationState.value = newElevation ?: _elevationState.value
                    }
                    is RangingResultPeerDisconnected -> {
                        // Cleanup
                        _distanceState.value = null
                        _azimuthState.value = null
                        _elevationState.value = null
                        onUWBConnectionLost()
                    }
                }
            }
        }
        if (job == null) {
            job = currentJob
        }
    }

    /*
     * Ends all established connections with uwb devices for cleanup.
     * This need to be called, because the viewmodel scope is different
     * from this coroutine scope, so destroying the viewmodel would not
     * destroy these coroutines and ranging would continue.
     */
    fun endUWBConnections() {
        job?.cancel()
    }
}