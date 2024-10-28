package com.example.uwbindoorpositioning.ui.screens.anchor

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.theme.dimensions
import com.example.uwbindoorpositioning.ui.theme.spacing

// This screen lets the user input the anchor's latitude, longitude, and compass bearing
@Composable
fun AnchorCoordinatesScreen(
    onStartButtonClicked: (anchorLatitude: String, anchorLongitude: String, anchorCompassBearing: String) -> Unit,
    viewModel: AnchorCoordinatesViewModel,
    modifier: Modifier = Modifier
) {
    // Input state
    val anchorLatitudeInputState = viewModel.anchorLatitudeInputState.collectAsStateWithLifecycle()
    val anchorLongitudeInputState = viewModel.anchorLongitudeInputState.collectAsStateWithLifecycle()
    val anchorCompassBearingInputState = viewModel.anchorCompassBearingInputState.collectAsStateWithLifecycle()
    val didUserEnteredValidLatitudeState = viewModel.didUserEnteredValidLatitudeState.collectAsStateWithLifecycle()
    val didUserEnteredValidLongitudeState = viewModel.didUserEnteredValidLongitudeState.collectAsStateWithLifecycle()
    val didUserEnteredValidCompassBearingState = viewModel.didUserEnteredValidCompassBearingState.collectAsStateWithLifecycle()

    // State variables
    val anchorLatitudeInput = anchorLatitudeInputState.value
    val anchorLongitudeInput = anchorLongitudeInputState.value
    val anchorCompassBearingInput = anchorCompassBearingInputState.value
    val didUserEnteredValidLatitude = didUserEnteredValidLatitudeState.value
    val didUserEnteredValidLongitude = didUserEnteredValidLongitudeState.value
    val didUserEnteredValidCompassBearing = didUserEnteredValidCompassBearingState.value

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.input_position),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.largeSpacerSize))
        InputPositionField(
            value = anchorLatitudeInput,
            onValueChange = { it -> viewModel.updateAnchorLatitudeInput(it) },
            label = stringResource(R.string.latitude_7_decimal_places),
            isError = !didUserEnteredValidLatitude,
            modifier = Modifier.fillMaxWidth(MaterialTheme.dimensions.inputPositionFieldWidthPercentage)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InputPositionField(
            value = anchorLongitudeInput,
            onValueChange = { it -> viewModel.updateAnchorLongitudeInput(it) },
            label = stringResource(R.string.longitude_7_decimal_places),
            isError = !didUserEnteredValidLongitude,
            modifier = Modifier.fillMaxWidth(MaterialTheme.dimensions.inputPositionFieldWidthPercentage)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.regularSpacerSize))
        InputPositionField(
            value = anchorCompassBearingInput,
            onValueChange = { it -> viewModel.updateAnchorCompassBearingInput(it) },
            label = stringResource(R.string.compass_bearing_0_359),
            isError = !didUserEnteredValidCompassBearing,
            modifier = Modifier.fillMaxWidth(MaterialTheme.dimensions.inputPositionFieldWidthPercentage)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.largeSpacerSize))
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
                .fillMaxWidth(MaterialTheme.dimensions.regularButtonWidthPercentage)
                .height(MaterialTheme.dimensions.regularButtonHeight)
        ) {
            Text(
                text = stringResource(R.string.start),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}