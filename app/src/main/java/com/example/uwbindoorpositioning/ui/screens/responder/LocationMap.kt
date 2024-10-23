package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.uwbindoorpositioning.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationMap(
    preciseLocation: LatLng,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(preciseLocation, 21f)
    }
    GoogleMap(
        properties = MapProperties(
            mapType = MapType.SATELLITE,
            isMyLocationEnabled = true
        ),
        cameraPositionState = cameraPositionState,
        modifier = modifier
    ) {
        Marker(
            state = MarkerState(position = preciseLocation),
            title = stringResource(R.string.marker_title),
            snippet = stringResource(R.string.marker_body),
            draggable = false
        )
    }
}