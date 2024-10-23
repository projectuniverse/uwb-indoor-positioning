package com.example.uwbindoorpositioning.ui.screens.anchor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.ConnectionAnimation
import com.example.uwbindoorpositioning.ui.screens.components.InfoCard
import com.example.uwbindoorpositioning.ui.theme.dimensions
import com.example.uwbindoorpositioning.ui.theme.spacing

@Composable
fun AnchorSearchScreen(
    viewModel: AnchorSearchViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        ConnectionAnimation(
            context = viewModel.context,
            modifier = Modifier.size(MaterialTheme.dimensions.connectionAnimationSize)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        Text(
            text = stringResource(R.string.searching_for_devices),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.largeSpacerSize))
        InfoCard(
            title = stringResource(R.string.anchors_latitude),
            body = viewModel.anchorLatitude,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InfoCard(
            title = stringResource(R.string.anchors_longitude),
            body = viewModel.anchorLongitude,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InfoCard(
            title = stringResource(R.string.anchors_compass_bearing),
            body = "${viewModel.anchorCompassBearing}°",
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
    }
}