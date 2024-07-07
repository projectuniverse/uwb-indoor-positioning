package com.example.uwbindoorpositioning.ui.screens.anchor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.ConnectionAnimation
import com.example.uwbindoorpositioning.ui.screens.components.InfoCard

@Composable
fun AnchorSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: AnchorViewModel,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        val latitudeInputState = viewModel.latitudeInputState.collectAsState()
        val longitudeInputState = viewModel.longitudeInputState.collectAsState()

        ConnectionAnimation(viewModel.getApplicationContext())
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
            body = latitudeInputState.value
        )
        Spacer(modifier = Modifier.height(10.dp))
        InfoCard(
            title = stringResource(R.string.anchors_longitude),
            body = longitudeInputState.value
        )
    }
}