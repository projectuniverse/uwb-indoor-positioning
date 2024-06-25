package com.example.uwbindoorpositioning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.example.uwbindoorpositioning.data.AppTheme
import com.example.uwbindoorpositioning.ui.UWBIndoorPositioningApp
import com.example.uwbindoorpositioning.ui.theme.UWBIndoorPositioningTheme
import dagger.hilt.android.AndroidEntryPoint

/*
 * @AndroidEntryPoint makes Hilt create a dependencies container which is
 * attached to MainActivity's lifecycle
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels() // Injected by Hilt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences = viewModel.userPreferencesFlow.collectAsState(initial = null)

            if (userPreferences.value != null) { // Make sure appTheme is known before displaying app
                val isDark = when (userPreferences.value!!.appTheme) {
                    AppTheme.MODE_AUTO -> isSystemInDarkTheme()
                    AppTheme.MODE_DAY -> false
                    AppTheme.MODE_NIGHT -> true
                }

                UWBIndoorPositioningTheme(darkTheme = isDark) {
                    UWBIndoorPositioningApp(
                        selectedTheme = userPreferences.value!!.appTheme,
                        setAppTheme = { appTheme -> viewModel.setAppTheme(appTheme) }
                    )
                }
            }
        }
    }
}