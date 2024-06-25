package com.example.uwbindoorpositioning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    // Preferences are accessible as a flow
    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    // Call function in new coroutine since setAppTheme is a suspend function
    fun setAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.setAppTheme(appTheme)
        }
    }
}