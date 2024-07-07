package com.example.uwbindoorpositioning.ui.screens.troubleshooting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uwbindoorpositioning.R

@Composable
fun UWBOffScreen(
    isDark: Boolean,
    viewModel: UWBOffViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Image(
            painter = if (isDark) {
                painterResource(R.drawable.wifi_off_white)
            } else {
                painterResource(R.drawable.wifi_off_grey)
            },
            contentDescription = null,
            modifier = Modifier.wrapContentSize(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.uwb_off),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(260.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { viewModel.openSettings() },
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