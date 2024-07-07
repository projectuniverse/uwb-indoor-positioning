package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.InfoCard
import com.google.android.gms.maps.model.LatLng

@Composable
fun ResponderScreen(
    isDark: Boolean,
    viewModel: ResponderViewModel,
    modifier: Modifier = Modifier,
) {
    val distanceState = viewModel.distanceState.collectAsState()
    val azimuthState = viewModel.azimuthState.collectAsState()
    val elevationState = viewModel.elevationState.collectAsState()
    val gpsLocationState = viewModel.gpsLocationState.collectAsState()
    val preciseLocationState = viewModel.preciseLocationState.collectAsState()
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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                // TODO Change connection animation if no connection found yet
                if (index == 0) {
                    RangingTab(
                        isDark = isDark,
                        distance = distanceState.value,
                        azimuth = azimuthState.value,
                        elevation = elevationState.value
                    )
                }
                else {
                    LocationTab(
                        gpsLocation = gpsLocationState.value,
                        preciseLocation = preciseLocationState.value
                    )
                }
            }
        }
    }
}

@Composable
fun RangingTab(
    isDark: Boolean,
    distance: Float,
    azimuth: Float,
    elevation: Float
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
            .rotate(azimuth),
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
    gpsLocation: LatLng,
    preciseLocation: LatLng
) {
    LocationMap(
        gpsLocation = gpsLocation,
        preciseLocation = preciseLocation
    )
    Spacer(modifier = Modifier.height(40.dp))
    InfoCard(
        title = stringResource(R.string.responders_latitude),
        body = preciseLocation.latitude.toString()
    )
    Spacer(modifier = Modifier.height(10.dp))
    InfoCard(
        title = stringResource(R.string.responders_longitude),
        body = preciseLocation.longitude.toString()
    )
}