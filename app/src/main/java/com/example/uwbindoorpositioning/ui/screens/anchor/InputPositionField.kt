package com.example.uwbindoorpositioning.ui.screens.anchor

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.uwbindoorpositioning.R

// Used in AnchorCoordinatesScreen to input latitude, longitude and compass bearing
@Composable
fun InputPositionField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        supportingText = {
        if (isError) {
            Text(text = stringResource(R.string.invalid_input))
        } else {
            Text(text = "")
        }},
        isError = isError,
        singleLine = true, // Makes sure the text box is a single line (even for multiple line input)
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}