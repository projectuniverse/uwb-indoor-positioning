package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResponderViewModel @Inject constructor() : ViewModel() {
    // TODO Add UWB logic here (search for anchors)
    // TODO cover case when data has not been received yet (such as azimuth)
    private val _distanceState = MutableStateFlow(0.0f)
    private val _azimuthState = MutableStateFlow(0.0f)
    private val _elevationState = MutableStateFlow(0.0f)
    private val _gpsLocationState = MutableStateFlow(LatLng(51.3112460,9.4740237))
    private val _preciseLocationState = MutableStateFlow(LatLng(51.3112320,9.4739010))
    val distanceState = _distanceState.asStateFlow()
    val azimuthState = _azimuthState.asStateFlow()
    val elevationState = _elevationState.asStateFlow()
    val gpsLocationState = _gpsLocationState.asStateFlow()
    val preciseLocationState = _preciseLocationState.asStateFlow()
}