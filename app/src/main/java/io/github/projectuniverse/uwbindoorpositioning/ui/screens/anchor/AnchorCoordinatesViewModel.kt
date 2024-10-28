package io.github.projectuniverse.uwbindoorpositioning.ui.screens.anchor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AnchorCoordinatesViewModel @Inject constructor() : ViewModel() {
    // Anchor input state
    private val _anchorLatitudeInputState = MutableStateFlow("")
    private val _anchorLongitudeInputState = MutableStateFlow("")
    private val _anchorCompassBearingInputState = MutableStateFlow("")
    private val _didUserEnteredValidLatitudeState = MutableStateFlow(true)
    private val _didUserEnteredValidLongitudeState = MutableStateFlow(true)
    private val _didUserEnteredValidCompassBearingState = MutableStateFlow(true)
    val anchorLatitudeInputState = _anchorLatitudeInputState.asStateFlow()
    val anchorLongitudeInputState = _anchorLongitudeInputState.asStateFlow()
    val anchorCompassBearingInputState = _anchorCompassBearingInputState.asStateFlow()
    val didUserEnteredValidLatitudeState = _didUserEnteredValidLatitudeState.asStateFlow()
    val didUserEnteredValidLongitudeState = _didUserEnteredValidLongitudeState.asStateFlow()
    val didUserEnteredValidCompassBearingState = _didUserEnteredValidCompassBearingState.asStateFlow()

    fun updateAnchorLatitudeInput(input: String) {
        if (doesCoordinateInputHaveCorrectFormat(input, true)) {
            _anchorLatitudeInputState.value = input
        }
    }

    fun updateAnchorLongitudeInput(input: String) {
        if (doesCoordinateInputHaveCorrectFormat(input, false)) {
            _anchorLongitudeInputState.value = input
        }
    }

    fun updateAnchorCompassBearingInput(input: String) {
        // Input length is restricted to 3 characters for highest value: 360
        val inputIntValue = input.toIntOrNull()
        if (input.length <= 3 &&
            !input.contains(" ") &&
            !input.contains(",") &&
            !input.contains("-") &&
            !input.contains(".") &&
            !(input.length >= 2 && input[0] == '0') &&
            !(inputIntValue != null && inputIntValue > 359)) {
            _anchorCompassBearingInputState.value = input
        }
    }

    /*
     * Returns true if the user entered valid inputs, otherwise false.
     * In case the user entered valid inputs, these are stores without leading zeros.
     * Also sets invalidInputsSubmittedState so that a hint message can be shown.
     */
    fun didUserEnterValidInputs(): Boolean {
        val anchorLatitude = _anchorLatitudeInputState.value.toDoubleOrNull()
        val anchorLongitude = _anchorLongitudeInputState.value.toDoubleOrNull()
        val anchorCompassBearing = _anchorCompassBearingInputState.value.toIntOrNull()
        val isValidLatitude = anchorLatitude != null && numberOfDecimalPlaces(_anchorLatitudeInputState.value) == 7
        val isValidLongitude = anchorLongitude != null && numberOfDecimalPlaces(_anchorLongitudeInputState.value) == 7
        val isValidCompassBearing = anchorCompassBearing != null

        _didUserEnteredValidLatitudeState.value = isValidLatitude
        _didUserEnteredValidLongitudeState.value = isValidLongitude
        _didUserEnteredValidCompassBearingState.value = isValidCompassBearing

        return isValidLatitude && isValidLongitude && isValidCompassBearing
    }

    private fun numberOfDecimalPlaces(input: String): Int {
        if (input.contains('.')) {
            val decimalPlaces = input.substring(input.indexOf('.') + 1)
            return decimalPlaces.length
        }
        else {
            return 0
        }
    }

    /*
     * Checks for various input restrictions for both coordinates.
     *
     * The 7th condition means: either the user has not entered a valid double value yet (e.g.,
     * the user entered a minus symbol when trying to enter a negative number) or the user has entered a
     * valid double number, in which case it can have at most 7 decimal places.
     */
    private fun doesCoordinateInputHaveCorrectFormat(input: String, isLatitude: Boolean): Boolean {
        val inputDoubleValue = input.toDoubleOrNull()
        var output = !input.contains(" ") &&
                !input.contains(",") &&
                !(input.isNotEmpty() && input[0] == '.') &&
                !(input.length >= 2 && input[0] == '0' && input[1] != '.') &&
                !(input.length >= 2 && input[0] == '-' && input[1] == '0') &&
                input.count { character -> character == '.' } <= 1 &&
                !(input.isNotEmpty() && input.substring(1).contains("-")) &&
                (inputDoubleValue == null || numberOfDecimalPlaces(input) <= 7)
        if (isLatitude) {
            // Input length is restricted to 11 characters for lowest value: -90.0000000
            output = output &&
                    input.length <= 11 &&
                    !(inputDoubleValue != null && (inputDoubleValue < -90 || inputDoubleValue > 90))
        }
        else {
            // Input length is restricted to 12 characters for lowest value: -180.0000000
            output = output &&
                    input.length <= 12 &&
                    !(inputDoubleValue != null && (inputDoubleValue < -180 || inputDoubleValue > 180))
        }
        return output
    }
}