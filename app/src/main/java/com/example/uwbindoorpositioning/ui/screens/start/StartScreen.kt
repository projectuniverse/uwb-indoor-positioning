package com.example.uwbindoorpositioning.ui.screens.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.theme.UWBIndoorPositioningTheme

@Composable
fun StartScreen(
    onUWBResponderButtonClicked: () -> Unit,
    onUWBAnchorButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
        ) {
            Text(
                text = stringResource(R.string.choose_role),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = onUWBResponderButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Text(
                    text = stringResource(R.string.uwb_responder),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onUWBAnchorButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Text(
                    text = stringResource(R.string.uwb_anchor),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun StartScreenPreview() {
    UWBIndoorPositioningTheme {
        StartScreen(
            onUWBAnchorButtonClicked = {},
            onUWBResponderButtonClicked = {},
            modifier = Modifier.fillMaxHeight()
        )
    }
}