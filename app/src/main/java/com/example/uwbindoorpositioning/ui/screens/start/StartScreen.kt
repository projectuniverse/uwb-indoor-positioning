package com.example.uwbindoorpositioning.ui.screens.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.theme.dimensions
import com.example.uwbindoorpositioning.ui.theme.spacing

@Composable
fun StartScreen(
    onUWBResponderButtonClicked: () -> Unit,
    onUWBAnchorButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.choose_role),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.largeSpacerSize))
        Button(
            onClick = onUWBResponderButtonClicked,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.largeButtonWidthPercentage)
                .height(MaterialTheme.dimensions.largeButtonHeight)
        ) {
            Text(
                text = stringResource(R.string.uwb_responder),
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        Button(
            onClick = onUWBAnchorButtonClicked,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.largeButtonWidthPercentage)
                .height(MaterialTheme.dimensions.largeButtonHeight)
        ) {
            Text(
                text = stringResource(R.string.uwb_anchor),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}