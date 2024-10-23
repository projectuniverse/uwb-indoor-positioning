package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.example.uwbindoorpositioning.ui.theme.dimensions
import com.example.uwbindoorpositioning.ui.theme.spacing

@Composable
fun ResponderSearchScreen(
    viewModel: ResponderViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        ConnectionAnimation(
            viewModel.context,
            modifier = Modifier.size(MaterialTheme.dimensions.connectionAnimationSize)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.largeSpacerSize))
        Text(
            text = stringResource(R.string.searching_for_devices),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}