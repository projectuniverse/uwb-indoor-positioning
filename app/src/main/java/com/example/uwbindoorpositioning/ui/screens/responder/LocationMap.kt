package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationMap(
    gpsLocation: LatLng,
    preciseLocation: LatLng
) {
    var isMapLoaded by remember { mutableStateOf(false) } // TODO do I need this?
    val bounds = createBounds(gpsLocation, preciseLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bounds.center, 20f)
    }

    GoogleMap(
        modifier = Modifier
            .width(300.dp)
            .height(300.dp),
        onMapLoaded = { isMapLoaded = true },
        cameraPositionState = cameraPositionState
    )
}

private fun createBounds(gpsLocation: LatLng, preciseLocation: LatLng): LatLngBounds {
    val boundsBuilder = LatLngBounds.builder()
    boundsBuilder.include(gpsLocation)
    boundsBuilder.include(preciseLocation)
    return boundsBuilder.build()
}
