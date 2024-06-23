package com.example.uwbindoorpositioning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.uwbindoorpositioning.ui.UWBIndoorPositioningApp
import com.example.uwbindoorpositioning.ui.theme.UWBIndoorPositioningTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UWBIndoorPositioningTheme() {
                UWBIndoorPositioningApp()
            }
        }
    }
}