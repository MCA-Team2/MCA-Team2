package com.jwpyo.soundmind

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jwpyo.soundmind.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(viewModelModule)
        }

        // Initialize the timezone information
        AndroidThreeTen.init(this)
    }
}