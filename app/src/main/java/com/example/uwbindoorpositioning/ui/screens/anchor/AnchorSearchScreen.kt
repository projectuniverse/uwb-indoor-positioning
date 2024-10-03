package com.example.uwbindoorpositioning.ui.screens.anchor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.ConnectionAnimation
import com.example.uwbindoorpositioning.ui.screens.components.InfoCard
import com.example.uwbindoorpositioning.ui.screens.troubleshooting.UWBRangingIncapableScreen

@Composable
fun AnchorSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: AnchorSearchViewModel
) {
    // State about device's own hardware capabilities
    val doesDeviceSupportUWBRangingState = viewModel.doesDeviceSupportUWBRangingState.collectAsStateWithLifecycle(initialValue = null)

    // State variables
    val doesDeviceSupportUWBRanging = doesDeviceSupportUWBRangingState.value

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        if (doesDeviceSupportUWBRanging != null) {
            if (!doesDeviceSupportUWBRanging) {
                UWBRangingIncapableScreen()
            }
            else {
                ConnectionAnimation(viewModel.context)
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = stringResource(R.string.searching_for_devices),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(300.dp)
                )
                Spacer(modifier = Modifier.height(60.dp))
                InfoCard(
                    title = stringResource(R.string.anchors_latitude),
                    body = viewModel.anchorLatitude
                )
                Spacer(modifier = Modifier.height(10.dp))
                InfoCard(
                    title = stringResource(R.string.anchors_longitude),
                    body = viewModel.anchorLongitude
                )
                Spacer(modifier = Modifier.height(10.dp))
                InfoCard(
                    title = stringResource(R.string.anchors_compass_bearing),
                    body = "${viewModel.anchorCompassBearing}°"
                )
            }
        }
    }
}