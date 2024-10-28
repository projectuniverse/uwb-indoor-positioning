package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.InfoCard
import com.example.uwbindoorpositioning.ui.theme.dimensions
import com.example.uwbindoorpositioning.ui.theme.spacing

/*
 * This screen shows an arrow that points towards the anchor's location. The screen also displays
 * the distance, azimuth and elevation to the anchor.
 */
@Composable
fun RangingTab(
    isDark: Boolean,
    distance: String,
    azimuth: String,
    elevation: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = if (isDark) {
                painterResource(R.drawable.arrow_upward_alt_white)
            } else {
                painterResource(R.drawable.arrow_upward_alt_black)
            },
            contentDescription = stringResource(R.string.arrow_pointing_towards_uwb_anchor),
            modifier = Modifier
                .size(MaterialTheme.dimensions.arrowIconSize)
                .rotate(azimuth.toFloat()),
            contentScale = ContentScale.Crop
        )
        InfoCard(
            title = stringResource(R.string.distance),
            body = "$distance m",
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InfoCard(
            title = stringResource(R.string.azimuth),
            body = "${azimuth}°",
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InfoCard(
            title = stringResource(R.string.elevation),
            body = "${elevation}°",
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
    }
}