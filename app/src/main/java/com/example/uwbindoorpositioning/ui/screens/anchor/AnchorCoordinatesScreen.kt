package com.example.uwbindoorpositioning.ui.screens.anchor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uwbindoorpositioning.R

@Composable
fun AnchorCoordinatesScreen(
    onStartButtonClicked: (anchorLatitude: String, anchorLongitude: String, anchorCompassBearing: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnchorCoordinatesViewModel
) {
    // Input state
    val anchorLatitudeInputState = viewModel.anchorLatitudeInputState.collectAsStateWithLifecycle()
    val anchorLongitudeInputState = viewModel.anchorLongitudeInputState.collectAsStateWithLifecycle()
    val anchorCompassBearingInputState = viewModel.anchorCompassBearingInputState.collectAsStateWithLifecycle()
    val areSubmittedInputsValidState = viewModel.areSubmittedInputsValidState.collectAsStateWithLifecycle()

    // State variables
    val anchorLatitudeInput = anchorLatitudeInputState.value
    val anchorLongitudeInput = anchorLongitudeInputState.value
    val anchorCompassBearingInput = anchorCompassBearingInputState.value
    val areSubmittedInputsValid = areSubmittedInputsValidState.value

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        if (!areSubmittedInputsValid) {
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
                text = stringResource(R.string.input_position),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            InputPositionField(
                value = anchorLatitudeInput,
                onValueChange = { it -> viewModel.updateAnchorLatitudeInput(it) },
                label = stringResource(R.string.latitude_7_decimal_places)
            )
            Spacer(modifier = Modifier.height(40.dp))
            InputPositionField(
                value = anchorLongitudeInput,
                onValueChange = { it -> viewModel.updateAnchorLongitudeInput(it) },
                label = stringResource(R.string.longitude_7_decimal_places)
            )
            Spacer(modifier = Modifier.height(40.dp))
            InputPositionField(
                value = anchorCompassBearingInput,
                onValueChange = { it -> viewModel.updateAnchorCompassBearingInput(it) },
                label = stringResource(R.string.compass_bearing_0_359)
            )
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                onClick = {
                    if (viewModel.didUserEnterValidInputs()) {
                        onStartButtonClicked(
                            anchorLatitudeInput,
                            anchorLongitudeInput,
                            anchorCompassBearingInput
                        )
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
        // TODO redo? Work with 1f or something like that
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
        )
    }
}

@Composable
private fun InputPositionField(
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
                imageVector = Icons.Rounded.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(R.string.invalid_inputs))
        }
    }
}