package com.example.quotevault

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuoteVaultApp : Application(){
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations here
    }
}