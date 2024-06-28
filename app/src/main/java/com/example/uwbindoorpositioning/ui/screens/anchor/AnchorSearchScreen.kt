package com.example.uwbindoorpositioning.ui.screens.anchor

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.uwbindoorpositioning.R
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AnchorSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: AnchorViewModel,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        val latitudeInputState = viewModel.latitudeInputState.collectAsState()
        val longitudeInputState = viewModel.longitudeInputState.collectAsState()

        ConnectionAnimation(viewModel.getApplicationContext())
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = stringResource(R.string.searching_for_devices),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(300.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))
        CoordinatesCard(
            title = stringResource(R.string.anchors_latitude),
            coordinate = latitudeInputState.value
        )
        Spacer(modifier = Modifier.height(10.dp))
        CoordinatesCard(
            title = stringResource(R.string.anchors_longitude),
            coordinate = longitudeInputState.value
        )
    }
}

@Composable
fun CoordinatesCard(
    title: String,
    coordinate: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = modifier
            .padding(10.dp)
            .height(80.dp)
            .width(300.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coordinate,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
            )
        }
    }
}

@Composable
fun ConnectionAnimation(
    context: Context
) {
    val duration = 1000
    val emptyDrawable = ContextCompat.getDrawable(context, R.drawable.empty_vector)
    val wifiOneBar = ContextCompat.getDrawable(context, R.drawable.sharp_symbol_wifi_1_bar_100_dp)
    val wifiTwoBar = ContextCompat.getDrawable(context, R.drawable.sharp_symbol_wifi_2_bars_100_dp)
    val wifiThreeBar = ContextCompat.getDrawable(context, R.drawable.sharp_symbol_wifi_3_bars_100_dp)
    val animationDrawable = AnimationDrawable()
    animationDrawable.addFrame(emptyDrawable!!,duration)
    animationDrawable.addFrame(wifiOneBar!!,duration)
    animationDrawable.addFrame(wifiTwoBar!!,duration)
    animationDrawable.addFrame(wifiThreeBar!!,duration)
    animationDrawable.setOneShot(false)

    Image(
        painter = rememberDrawablePainter(animationDrawable),
        contentDescription = null,
        modifier = Modifier.wrapContentSize(),
        contentScale = ContentScale.Crop
    )

    animationDrawable.start()
}