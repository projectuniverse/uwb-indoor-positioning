package io.github.projectuniverse.uwbindoorpositioning.ui.screens.components

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import io.github.projectuniverse.uwbindoorpositioning.R
import com.google.accompanist.drawablepainter.rememberDrawablePainter

/*
 * Composable that is used by multiple screens. It displays an animation that shows a
 * moving radio wave icon.
 */
@Composable
fun ConnectionAnimation(
    context: Context,
    modifier: Modifier = Modifier
) {
    val duration = 1000
    val emptyDrawable = ContextCompat.getDrawable(context, R.drawable.empty_vector)
    val wifiOneBar = ContextCompat.getDrawable(context, R.drawable.wifi_1_bar)
    val wifiTwoBar = ContextCompat.getDrawable(context, R.drawable.wifi_2_bars)
    val wifiThreeBar = ContextCompat.getDrawable(context, R.drawable.wifi_3_bars)
    val animationDrawable = AnimationDrawable()
    animationDrawable.addFrame(emptyDrawable!!,duration)
    animationDrawable.addFrame(wifiOneBar!!,duration)
    animationDrawable.addFrame(wifiTwoBar!!,duration)
    animationDrawable.addFrame(wifiThreeBar!!,duration)
    animationDrawable.setOneShot(false)

    Image(
        painter = rememberDrawablePainter(animationDrawable),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )

    animationDrawable.start()
}