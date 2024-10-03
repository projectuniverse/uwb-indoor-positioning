package com.example.uwbindoorpositioning.connections

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

class AnchorNearbyConnector @Inject constructor(
    @ApplicationContext val context: Context
) {
    /*
     * Strategy for telling the Nearby Connections API how we want to discover and connect to
     * other nearby devices (in our case: many to many). Must be the same for anchor and responder.
     */
    private val strategy = Strategy.P2P_CLUSTER
    // Our handle to the Nearby Connections API ConnectionsClient
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)

    // Data relevant for establishing the UWB connection
    private var anchorNearbyPayload: AnchorNearbyPayload? = null
    private var onNearbyConnectionEstablished: (ResponderNearbyPayload) -> Unit = {}

    private var wasNearbyConnectionStoppedIntentionally = false

    private var currentAnchorNearbyPayloadId = 0L
    private var currentConnectingResponderEndpointId = ""

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
         * Since the responder resends its payload if the anchor has not received it,
         * we can be sure that once the anchor has established a connection with the responder
         * and does not lose it, the anchor will definitely receive a payload from it.
         */
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {
                val responderNearbyPayload = Json.decodeFromString<ResponderNearbyPayload>(String(it, UTF_8))
                currentConnectingResponderEndpointId = ""
                /*
                 * End nearby connection, because it is no longer needed. This step is crucial in case
                 * the responder loses the UWB connection to the anchor and wants to reconnect. The responder
                 * can only reconnect if it is not already connected to the anchor via nearby connections.
                 */
                connectionsClient.disconnectFromEndpoint(endpointId)
                onNearbyConnectionEstablished(responderNearbyPayload)
            }
        }
        /*
         * Called for ingoing or outgoing payloads and provides progress updates (SUCCESS = received).
         * Calling onNearbyConnectionEstablished here when update.status == PayloadTransferUpdate.Status.SUCCESS
         * would cause startRanging to be called twice due to the outgoing payload.
         *
         * In case our anchorNearbyPayload has not been received, we send it again.
         */
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.payloadId == currentAnchorNearbyPayloadId &&
                (update.status == PayloadTransferUpdate.Status.FAILURE ||
                update.status == PayloadTransferUpdate.Status.CANCELED)) {
                sendData(endpointId, anchorNearbyPayload!!)
            }
        }
    }
    // Callbacks for connections to other devices
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        // Tells us that someone has noticed the advertisement and wants to connect.
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
                * Stopping the advertisement here apparently ensures that the anchor is only
                * connected to one new responder for the given anchorNearbyPayload.
                * This is the official approach by Google.
                * See: https://developer.android.com/codelabs/nearby-connections#3
                */
                connectionsClient.stopAdvertising()
                currentConnectingResponderEndpointId = endpointId
                sendData(endpointId, anchorNearbyPayload!!)
            }
        }
        // Called if connection is no longer active
        override fun onDisconnected(endpointId: String) {
            if (!wasNearbyConnectionStoppedIntentionally &&
                currentConnectingResponderEndpointId == endpointId) {
                //  new advertising has already started
                startAdvertising(
                    anchorNearbyPayload = anchorNearbyPayload!!,
                    onNearbyConnectionEstablished = onNearbyConnectionEstablished
                )
            }
        }
    }

    // Called by anchor to exchange data and establish UWB connection
    private fun sendData(endpointId: String, anchorNearbyPayload: AnchorNearbyPayload) {
        val payload = Payload.fromBytes(Json.encodeToString(anchorNearbyPayload).toByteArray(UTF_8))
        currentAnchorNearbyPayloadId = payload.id
        connectionsClient.sendPayload(
            endpointId,
            payload
        )
    }

    /*
     * Tells Nearby Connections API that we want to enter advertising mode.
     * Also sets the anchorNearbyPayload.
     */
    fun startAdvertising(
        anchorNearbyPayload: AnchorNearbyPayload,
        onNearbyConnectionEstablished: (ResponderNearbyPayload) -> Unit
    ) {
        this.anchorNearbyPayload = anchorNearbyPayload
        this.onNearbyConnectionEstablished = onNearbyConnectionEstablished
        val options = AdvertisingOptions.Builder().setStrategy(strategy).build()
        try {
            connectionsClient.startAdvertising(
                "Anchor",
                context.packageName,
                connectionLifecycleCallback,
                options
            )
        } catch (e: Exception) {
            Log.e("AnchorNearbyConnector", "Could not start advertising")
        }
    }

    /*
     * Ends all established connections with nearby devices and the search for nearby devices.
     *
     * Note: stopDiscovery and stopAllEndpoints might take some time, so all code afterward could
     * happen before onDisconnected callback is triggered by stopAdvertising and stopAllEndpoints.
     */
    fun endAllNearbyConnections() {
        wasNearbyConnectionStoppedIntentionally = true
        connectionsClient.apply {
            stopAdvertising()
            stopAllEndpoints()
        }
    }
}