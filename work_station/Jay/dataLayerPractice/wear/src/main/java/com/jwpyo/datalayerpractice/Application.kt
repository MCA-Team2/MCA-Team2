package com.jwpyo.datalayerpractice

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jwpyo.datalayerpractice.di.viewModelModule
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