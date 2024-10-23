package com.example.uwbindoorpositioning.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// This file uses 8 dp increments due to the 8 dp grid system.

data class Padding(
    val verticalPadding: Dp,
    val horizontalPadding: Dp
)

val compactWidthPadding = Padding(
    verticalPadding = 16.dp,
    horizontalPadding = 24.dp
)

val mediumWidthPadding = Padding(
    verticalPadding = 16.dp,
    horizontalPadding = 48.dp
)

val expandedWidthPadding = Padding(
    verticalPadding = 16.dp,
    horizontalPadding = 72.dp
)

val LocalPadding = compositionLocalOf { compactWidthPadding }

val MaterialTheme.padding: Padding
    @Composable
    @ReadOnlyComposable
    get() = LocalPadding.current