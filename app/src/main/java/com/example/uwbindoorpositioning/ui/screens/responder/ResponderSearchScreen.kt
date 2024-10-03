package com.example.uwbindoorpositioning.ui.screens.responder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uwbindoorpositioning.R
import com.example.uwbindoorpositioning.ui.screens.components.ConnectionAnimation

@Composable
fun ResponderSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: ResponderViewModel
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ConnectionAnimation(viewModel.context)
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = stringResource(R.string.searching_for_devices),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(300.dp)
        )
    }
}