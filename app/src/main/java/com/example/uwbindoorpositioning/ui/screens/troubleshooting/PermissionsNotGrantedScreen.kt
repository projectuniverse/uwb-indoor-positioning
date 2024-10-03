package com.example.uwbindoorpositioning.ui.screens.troubleshooting

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uwbindoorpositioning.R

@Composable
fun PermissionsNotGrantedScreen(
    viewModel: PermissionsNotGrantedViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.permissions_not_granted),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(260.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { viewModel.openAppSettings() },
            modifier = Modifier
                .width(200.dp)
                .height(55.dp)
        ) {
            Text(
                text = stringResource(R.string.go_to_settings),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp)
            )
        }
    }
}