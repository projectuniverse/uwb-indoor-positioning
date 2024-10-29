package io.github.projectuniverse.uwbindoorpositioning.ui.screens.troubleshooting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.projectuniverse.uwbindoorpositioning.R
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.dimensions
import io.github.projectuniverse.uwbindoorpositioning.ui.theme.spacing

// An enum class that represents the different required actions
enum class RequiredUserAction() {
    ACTION_GRANT_PERMISSIONS,
    ACTION_ENABLE_UWB,
    ACTION_TURN_ON_LOCATION;
}

// This screen is shown when the user has not granted the app all required permissions
@Composable
fun UserActionRequiredScreen(
    requiredUserAction: RequiredUserAction,
    viewModel: UserActionRequiredViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        val text: String
        val onClick: () -> Unit
        when (requiredUserAction) {
            RequiredUserAction.ACTION_GRANT_PERMISSIONS -> {
                text = stringResource(R.string.permissions_not_granted)
                onClick = { viewModel.openAppSettings() }
            }
            RequiredUserAction.ACTION_ENABLE_UWB -> {
                text = stringResource(R.string.uwb_unavailable)
                onClick = { viewModel.openSettings() }
            }
            RequiredUserAction.ACTION_TURN_ON_LOCATION -> {
                text = stringResource(R.string.location_turned_off)
                onClick = { viewModel.openLocationSettings() }
            }
        }
        Icon(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimensions.errorIconSize)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.largeSpacerSize))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.largeSpacerSize))
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(MaterialTheme.dimensions.regularButtonWidthPercentage)
                .height(MaterialTheme.dimensions.regularButtonHeight)
        ) {
            Text(
                text = stringResource(R.string.go_to_settings),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}