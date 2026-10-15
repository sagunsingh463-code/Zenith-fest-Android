package com.zenith26.fest

import android.app.Application
import com.zenith26.fest.data.api.ApiClient

/**
 * Application entry point for Zenith '26 Android Application.
 */
class ZenithApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
    }
}
