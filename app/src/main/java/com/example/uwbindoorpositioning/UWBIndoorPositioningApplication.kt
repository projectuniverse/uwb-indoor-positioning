package com.example.uwbindoorpositioning

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
 * A container (class that is in charge of providing dependencies) that is attached
 * to the app's lifecycle. @HiltAndroidApp allows for Hilt's code generation.
 */
@HiltAndroidApp
class UWBIndoorPositioningApplication : Application()