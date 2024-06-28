package com.example.uwbindoorpositioning.ui.screens.anchor

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/*
 * This ViewModel must be used by both anchor screens, so that the location data
 * does not get destroyed when the screens are switched
 */

// TODO Put UWB Logic here (establish connections)
@HiltViewModel
class AnchorViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _latitudeInputState = MutableStateFlow("")
    private val _longitudeInputState = MutableStateFlow("")
    val latitudeInputState = _latitudeInputState.asStateFlow()
    val longitudeInputState = _longitudeInputState.asStateFlow()
    private val _invalidInputsSubmittedState = MutableStateFlow(false)
    val invalidInputsSubmittedState = _invalidInputsSubmittedState.asStateFlow()

    fun updateLatitudeInput(input: String) {
        /*
         * Input length is restricted to 11 for lowest value: -90.0000000
         *
         * Either the user has not entered a valid double value yet (e.g., the user entered a
         * minus symbol when trying to enter a negative number) or the user has entered a
         * valid double number, in which case it can have at most 7 decimal places.
         */
        if (input.length <= 11 && (input.toDoubleOrNull() == null || numberOfDecimalPlaces(input) <= 7)) {
            _latitudeInputState.value = input
        }
    }

    fun updateLongitudeInput(input: String) {
        // Input length is restricted to 12 for lowest value: -180.0000000
        if (input.length <= 12 && (input.toDoubleOrNull() == null || numberOfDecimalPlaces(input) <= 7)) {
            _longitudeInputState.value = input
        }
    }

    /*
     * Returns true if the user entered valid coordinates, otherwise false.
     * In case the user entered valid coordinates, these are stores without leading zeros.
     * Also sets invalidInputsSubmittedState so that a hint message can be shown.
     */
    fun userEnteredValidCoordinates(): Boolean {
        val latitude = _latitudeInputState.value.toDoubleOrNull()
        val longitude = _longitudeInputState.value.toDoubleOrNull()
        val userEnteredValidLatitude =
            latitude != null && latitude >= -90 && latitude <= 90 && numberOfDecimalPlaces(_latitudeInputState.value) == 7
        val userEnteredValidLongitude =
            longitude != null && longitude >= -180 && longitude <= 180 && numberOfDecimalPlaces(_longitudeInputState.value) == 7
        if (userEnteredValidLatitude && userEnteredValidLongitude) {
            _latitudeInputState.value = formatCoordinate(_latitudeInputState.value)
            _longitudeInputState.value = formatCoordinate(_longitudeInputState.value)
            _invalidInputsSubmittedState.value = false
            return true
        }
        else {
            _invalidInputsSubmittedState.value = true
            return false
        }
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
     * This function:
     * - removes all leading zeros
     * - removes a minus symbol if the number before the decimal point is a zero
     * - keeps all decimal places, even if they are zeros (unlike toDoubleOrNull())
     */
    private fun formatCoordinate(coordinate: String): String {
        // Part before the decimal point
        val wholeNumberWithoutLeadingZeros = coordinate.substring(0, coordinate.indexOf('.')).toInt().toString()
        val fractionalPart = coordinate.substring(coordinate.indexOf('.') + 1)
        return "$wholeNumberWithoutLeadingZeros.$fractionalPart"
    }

    fun getApplicationContext(): Context {
        return context
    }
}