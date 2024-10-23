package com.example.uwbindoorpositioning.connections

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

class ResponderNearbyConnector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /*
     * Strategy for telling the Nearby Connections API how we want to discover and connect to
     * other nearby devices (in our case: many to many). Must be the same for anchor and responder.
     */
    private val strategy = Strategy.P2P_CLUSTER
    // Our handle to the Nearby Connections API ConnectionsClient
    private var connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)

    // Data relevant for establishing the UWB connection
    private var responderUWBSessionData: ResponderUWBSessionData? = null
    private var onNearbyConnectionEstablished: (AnchorUWBSessionData) -> Unit = {}
    private var responderNearbyPayload: ResponderNearbyPayload? = null

    private var wasNearbyConnectionStoppedIntentionally = false

    // Anchor state
    private val _anchorLatitudeState = MutableStateFlow<Double?>(null)
    private val _anchorLongitudeState = MutableStateFlow<Double?>(null)
    private val _anchorCompassBearingState = MutableStateFlow<Int?>(null)
    val anchorLatitudeState = _anchorLatitudeState.asStateFlow()
    val anchorLongitudeState = _anchorLongitudeState.asStateFlow()
    val anchorCompassBearingState = _anchorCompassBearingState.asStateFlow()

    private var currentResponderNearbyPayloadId = 0L
    private var currentConnectingAnchorEndpointId = ""

    // Callback for receiving payloads
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        /*
         * Read incoming messages.
         * Note: This function is called when the first byte is received.
         * Generally, it does not indicate that the entire payload has been received.
         * However, BYTES payloads are sent as a single chunk. Thus, we don't need to wait
         * for the SUCCESS update in onPayloadTransferUpdate.
         * See: https://developers.google.com/nearby/connections/android/exchange-data
         *
         * Since the anchor resends its payload if the responder has not received it,
         * we can be sure that once the responder has established a connection with the anchor
         * and does not lose it, the responder will definitely receive a payload from it.
         */
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {
                val newAnchorNearbyPayload = Json.decodeFromString<AnchorNearbyPayload>(String(it, UTF_8))
                currentConnectingAnchorEndpointId = ""
                val anchorUWBSessionData = newAnchorNearbyPayload.anchorUWBSessionData
                _anchorLatitudeState.value = newAnchorNearbyPayload.anchorLatitude
                _anchorLongitudeState.value = newAnchorNearbyPayload.anchorLongitude
                _anchorCompassBearingState.value = newAnchorNearbyPayload.anchorCompassBearing
                responderNearbyPayload = ResponderNearbyPayload(
                    responderUWBSessionData = responderUWBSessionData!!,
                    anchorUWBSessionData = anchorUWBSessionData
                )
                sendData(endpointId, responderNearbyPayload!!)
                onNearbyConnectionEstablished(anchorUWBSessionData)
            }
        }
        /*
         * Called for ingoing or outgoing payloads and provides progress updates (SUCCESS = received).
         * Calling onNearbyConnectionEstablished here when update.status == PayloadTransferUpdate.Status.SUCCESS
         * would cause startRanging to be called twice due to the outgoing payload.
         *
         * In case our responderNearbyPayload has not been received, we send it again.
         */
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.payloadId == currentResponderNearbyPayloadId &&
                (update.status == PayloadTransferUpdate.Status.FAILURE ||
                update.status == PayloadTransferUpdate.Status.CANCELED)) {
                sendData(endpointId, responderNearbyPayload!!)
            }
        }
    }
    // Callbacks for connections to other devices
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        // Tells us that someone has noticed us and wants to connect
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            /*
             * Accepting a connection means we want to receive messages.
             * We attach the PayloadCall to the acceptance.
             */
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }
        /*
         * Called after both sides have either accepted or rejected the connection.
         * Lets us know whether the connection was established.
         */
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                /*
                 * Note: I could not find anything about whether this approach is thread safe.
                 * Stopping the discovery here apparently ensures that the responder is only
                 * connected to one new anchor. This is the official approach by Google.
                 * See: https://developer.android.com/codelabs/nearby-connections#3
                 */
                connectionsClient.stopDiscovery()
                currentConnectingAnchorEndpointId = endpointId
            }
        }
        // Called if connection is no longer active.
        override fun onDisconnected(endpointId: String) {
            if (!wasNearbyConnectionStoppedIntentionally &&
                currentConnectingAnchorEndpointId == endpointId) {
                //  new advertising has already started
                startDiscovery(
                    responderUWBSessionData = responderUWBSessionData!!,
                    onNearbyConnectionEstablished = onNearbyConnectionEstablished
                )
            }
        }
    }
    // Callbacks for finding other devices (discovery, responder)
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        // Called every time an advertisement is detected
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection("Responder", endpointId, connectionLifecycleCallback)
        }
        // Called when an advertisement is no longer discoverable
        override fun onEndpointLost(endpointId: String) {}
    }

    // Called by anchor and responder to exchange data and establish UWB connection
    private fun sendData(endpointId: String, responderNearbyPayload: ResponderNearbyPayload) {
        val payload = Payload.fromBytes(Json.encodeToString(responderNearbyPayload).toByteArray(UTF_8))
        currentResponderNearbyPayloadId = payload.id
        connectionsClient.sendPayload(
            endpointId,
            payload
        )
    }

    /*
     * Tells Nearby Connections API that we want to enter discovery mode.
     * Also sets the responderUWBSessionData.
     */
    fun startDiscovery(
        responderUWBSessionData: ResponderUWBSessionData,
        onNearbyConnectionEstablished: (AnchorUWBSessionData) -> Unit
    ) {
        this.responderUWBSessionData = responderUWBSessionData
        this.onNearbyConnectionEstablished = onNearbyConnectionEstablished
        val options = DiscoveryOptions.Builder().setStrategy(strategy).build()
        try {
            connectionsClient.startDiscovery(
                context.packageName,
                endpointDiscoveryCallback,
                options
            )
        } catch (e: Exception) {
            Log.e("ResponderNearbyConnector", "Could not start discovery")
        }
    }

    /*
     * Ends all established connections with nearby devices and the search for nearby devices.
     *
     * Note: stopDiscovery and stopAllEndpoints might take some time, so all code afterwards could
     * happen before onDisconnected callback is triggered by stopDiscovery and stopAllEndpoints.
     */
    fun endNearbyConnections() {
        wasNearbyConnectionStoppedIntentionally = true
        connectionsClient.apply {
            stopDiscovery()
            stopAllEndpoints()
        }
    }
}