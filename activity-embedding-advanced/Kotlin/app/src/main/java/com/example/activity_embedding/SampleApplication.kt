package com.example.activity_embedding

import android.app.Application

/**
 * Initializer for activity embedding split rules.
 */
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SplitManager.createSplit(this)
    }
}