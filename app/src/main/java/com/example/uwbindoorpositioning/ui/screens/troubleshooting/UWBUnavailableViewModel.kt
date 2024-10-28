package com.example.uwbindoorpositioning.ui.screens.troubleshooting

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UWBUnavailableViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    fun openSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(context, intent, null)
    }
}