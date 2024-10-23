package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBErrorScreen

@Composable
fun ResponderScreen(
    isDark: Boolean,
    viewModel: ResponderViewModel,
    modifier: Modifier = Modifier
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

    if (doesDeviceSupportUWBRanging != null) {
        if (!doesDeviceSupportUWBRanging) {
            UWBErrorScreen(
                errorMessage = stringResource(R.string.device_does_not_support_uwb_ranging),
                modifier = modifier
            )
        } else {
            if (!isAllRelevantAnchorDataAvailable) {
                ResponderSearchScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                ) {
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
                                elevation = formattedElevation,
                                modifier = Modifier.fillMaxSize()
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
                                preciseLongitude = preciseLocation[1],
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}