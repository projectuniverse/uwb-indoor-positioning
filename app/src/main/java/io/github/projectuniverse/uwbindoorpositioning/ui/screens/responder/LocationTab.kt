package io.github.projectuniverse.uwbindoorpositioning.ui.screens.responder

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
import androidx.compose.ui.res.stringResource
import io.github.projectuniverse.uwbindoorpositioning.R
import io.github.projectuniverse.uwbindoorpositioning.ui.screens.components.InfoCard
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.dimensions
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.spacing
import com.google.android.gms.maps.model.LatLng

// This screen shows the responder's location on a map, as well as its precise latitude and longitude
@Composable
fun LocationTab(
    preciseLatitude: String,
    preciseLongitude: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        LocationMap(
            preciseLocation = LatLng(
                preciseLatitude.toDouble(),
                preciseLongitude.toDouble()
            ),
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.mapWidthPercentage)
                .height(MaterialTheme.dimensions.mapHeight)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InfoCard(
            title = stringResource(R.string.responders_latitude),
            body = preciseLatitude,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InfoCard(
            title = stringResource(R.string.responders_longitude),
            body = preciseLongitude,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.infoCardWidthPercentage)
                .height(MaterialTheme.dimensions.infoCardHeight)
        )
    }
}