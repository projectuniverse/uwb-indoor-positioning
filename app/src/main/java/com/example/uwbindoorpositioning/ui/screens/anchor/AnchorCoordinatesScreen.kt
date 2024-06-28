package com.example.uwbindoorpositioning.ui.screens.anchor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uwbindoorpositioning.R

@Composable
fun AnchorCoordinatesScreen(
    onStartButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnchorViewModel
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        val latitudeInputState = viewModel.latitudeInputState.collectAsState()
        val longitudeInputState = viewModel.longitudeInputState.collectAsState()
        val invalidInputsSubmittedState = viewModel.invalidInputsSubmittedState.collectAsState()

        if (invalidInputsSubmittedState.value) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
            ) {
                InvalidInputHint(
                    modifier = Modifier
                        .height(100.dp)
                        .width(350.dp)
                        .padding(10.dp)
                )
            }
        }
        else {
            /*
             * Show empty spacer so that when the actual invalid input message is shown,
             * the input fields are not moved downwards.
             */
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.input_coordinates),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            InputCoordinateField(
                value = latitudeInputState.value,
                onValueChange = { it -> viewModel.updateLatitudeInput(it) },
                label = stringResource(R.string.latitude)
            )
            Spacer(modifier = Modifier.height(40.dp))
            InputCoordinateField(
                value = longitudeInputState.value,
                onValueChange = { it -> viewModel.updateLongitudeInput(it) },
                label = stringResource(R.string.longitude)
            )
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                onClick = {
                    if (viewModel.userEnteredValidCoordinates()) {
                        onStartButtonClicked()
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(80.dp)
            ) {
                Text(
                    text = stringResource(R.string.start),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp)
                )
            }
        }
        /*
         * Empty spacer to ensure that input fields are centered and invalid input
         * message is at top, without covering the input fields. This works due to the
         * nature of verticalArrangement = Arrangement.SpaceBetween.
         */
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
        )
    }
}

@Composable
private fun InputCoordinateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true, // Makes sure the text box is a single line (even for multiple line input)
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Composable
fun InvalidInputHint(
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        modifier = modifier
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(R.string.invalid_inputs))
        }
    }
}