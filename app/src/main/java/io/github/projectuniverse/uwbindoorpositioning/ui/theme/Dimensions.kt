package io.github.projectuniverse.uwbindoorpositioning.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// This file uses 8 dp increments due to the 8 dp grid system.
data class Dimensions(
    val errorIconSize: Dp = 120.dp,
    val arrowIconSize: Dp = 280.dp,
    val connectionAnimationSize: Dp = 120.dp,

    val regularButtonWidthPercentage: Float = 1f,
    val regularButtonHeight: Dp = 64.dp,
    val largeButtonWidthPercentage: Float = 1f,
    val largeButtonHeight: Dp = 80.dp,

    val inputPositionFieldWidthPercentage: Float = 1f,

    val infoCardWidthPercentage: Float = 1f,
    val infoCardHeight: Dp = 88.dp,

    val mapWidthPercentage: Float = 1f,
    val mapHeight: Dp = 360.dp,
)

val LocalDimensions = compositionLocalOf { Dimensions() }

val MaterialTheme.dimensions: Dimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current