package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.InfoCard
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBRangingIncapableScreen
import com.google.android.gms.maps.model.LatLng

@Composable
fun ResponderScreen(
    isDark: Boolean,
    viewModel: ResponderViewModel,
    modifier: Modifier = Modifier,
) {
    // State about device's own hardware capabilities
    val doesDeviceSupportUWBRangingState = viewModel.doesDeviceSupportUWBRangingState.collectAsStateWithLifecycle(initialValue = null)

    // UWB ranging state
    val distanceState = viewModel.distanceState.collectAsStateWithLifecycle(initialValue = null)
    val azimuthState = viewModel.azimuthState.collectAsStateWithLifecycle(initialValue = null)
    val elevationState = viewModel.elevationState.collectAsStateWithLifecycle(initialValue = null)
    
    // Anchor state
    val anchorLatitudeState = viewModel.anchorLatitudeState.collectAsStateWithLifecycle(initialValue = null)
    val anchorLongitudeState = viewModel.anchorLongitudeState.collectAsStateWithLifecycle(initialValue = null)
    val anchorCompassBearingState = viewModel.anchorCompassBearingState.collectAsStateWithLifecycle(initialValue = null)

    // State variables
    val doesDeviceSupportUWBRanging = doesDeviceSupportUWBRangingState.value
    val distance = distanceState.value
    val azimuth = azimuthState.value
    val elevation = elevationState.value
    val anchorLatitude = anchorLatitudeState.value
    val anchorLongitude = anchorLongitudeState.value
    val anchorCompassBearing = anchorCompassBearingState.value

    val isAllRelevantAnchorDataAvailable =
                distance != null && azimuth != null && elevation != null &&
                anchorLatitude != null && anchorLongitude != null && anchorCompassBearing != null

    val tabs = listOf(stringResource(R.string.ranging), stringResource(R.string.location))
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabs.size }

    // Triggers when user clicks tab
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    // Triggers when user swipes to tab
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 20.dp)
        // TODO Add vertical scroll?
    ) {
        // TODO which composables should get Modifier.fillMaxSize()?
        if (doesDeviceSupportUWBRanging != null) {
            if (!doesDeviceSupportUWBRanging) {
                UWBRangingIncapableScreen()
            }
            else {
                if (!isAllRelevantAnchorDataAvailable) {
                    ResponderSearchScreen(
                        viewModel = viewModel
                    )
                }
                else {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, item ->
                            Tab(
                                selected = index == selectedTabIndex,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = item) }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Inside Column, HorizontalPager gets all space left besides TabRow
                    ) { index ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxSize()
                        ) {
                            val formattedDistance = viewModel.formatDecimalNumber(
                                decimalNumber = distance!!.toDouble(),
                                numberOfDecimalPlaces = 2
                            )
                            val formattedAzimuth = viewModel.formatDecimalNumber(
                                decimalNumber = azimuth!!.toDouble(),
                                numberOfDecimalPlaces = 2,
                                minValue = -90.0,
                                maxValue = 90.0
                            )
                            val formattedElevation = viewModel.formatDecimalNumber(
                                decimalNumber = elevation!!.toDouble(),
                                numberOfDecimalPlaces = 2,
                                minValue = -90.0,
                                maxValue = 90.0
                            )
                            if (index == 0) {
                                RangingTab(
                                    isDark = isDark,
                                    distance = formattedDistance,
                                    azimuth = formattedAzimuth,
                                    elevation = formattedElevation
                                )
                            } else {
                                val preciseLocation = viewModel.getPreciseLocation(
                                    distance = formattedDistance.toFloat(),
                                    azimuth = formattedAzimuth.toFloat(),
                                    elevation = formattedElevation.toFloat(),
                                    anchorLatitude = anchorLatitude!!,
                                    anchorLongitude = anchorLongitude!!,
                                    anchorCompassBearing = anchorCompassBearing!!
                                )
                                LocationTab(
                                    preciseLatitude = preciseLocation[0],
                                    preciseLongitude = preciseLocation[1]
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RangingTab(
    isDark: Boolean,
    distance: String,
    azimuth: String,
    elevation: String
) {
    Image(
        painter = if (isDark) {
            painterResource(R.drawable.arrow_upward_alt_white)
        } else {
            painterResource(R.drawable.arrow_upward_alt_black)
        },
        contentDescription = stringResource(R.string.arrow_upward),
        modifier = Modifier
            .wrapContentSize()
            .rotate(azimuth.toFloat()),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(60.dp))
    InfoCard(
        title = stringResource(R.string.distance),
        body = "$distance m"
    )
    Spacer(modifier = Modifier.height(10.dp))
    InfoCard(
        title = stringResource(R.string.azimuth),
        body = "${azimuth}°"
    )
    Spacer(modifier = Modifier.height(10.dp))
    InfoCard(
        title = stringResource(R.string.elevation),
        body = "${elevation}°"
    )
}

@Composable
fun LocationTab(
    preciseLatitude: String,
    preciseLongitude: String
) {
    LocationMap(
        preciseLocation = LatLng(
            preciseLatitude.toDouble(),
            preciseLongitude.toDouble()
        )
    )
    Spacer(modifier = Modifier.height(40.dp))
    InfoCard(
        title = stringResource(R.string.responders_latitude),
        body = preciseLatitude
    )
    Spacer(modifier = Modifier.height(10.dp))
    InfoCard(
        title = stringResource(R.string.responders_longitude),
        body = preciseLongitude
    )
}