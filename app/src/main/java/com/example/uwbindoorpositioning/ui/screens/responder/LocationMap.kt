package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.uwbindoorpositioning.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationMap(
    preciseLocation: LatLng
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(preciseLocation, 21f)
    }
    GoogleMap(
        modifier = Modifier
            .width(350.dp)
            .height(400.dp),
        properties = MapProperties(
            mapType = MapType.SATELLITE,
            isMyLocationEnabled = true
        ),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = preciseLocation),
            title = stringResource(R.string.marker_title),
            snippet = stringResource(R.string.marker_body),
            draggable = false
        )
    }
}
