package com.example.composeplayground

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PlayGroundApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
