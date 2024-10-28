package io.github.projectuniverse.uwbindoorpositioning.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// This file uses 8 dp increments due to the 8 dp grid system.
data class Spacing(
    val topAppBarSmallSpacerSize: Dp = 8.dp,
    val smallSpacerSize: Dp,
    val regularSpacerSize: Dp,
    val largeSpacerSize: Dp,
)

val compactHeightSpacing = Spacing(
    smallSpacerSize = 16.dp,
    regularSpacerSize = 16.dp,
    largeSpacerSize = 48.dp
)

val mediumHeightSpacing = Spacing(
    smallSpacerSize = 24.dp,
    regularSpacerSize = 24.dp,
    largeSpacerSize = 64.dp
)

val expandedHeightSpacing = Spacing(
    smallSpacerSize = 32.dp,
    regularSpacerSize = 32.dp,
    largeSpacerSize = 72.dp
)

val LocalSpacing = compositionLocalOf { compactHeightSpacing }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current