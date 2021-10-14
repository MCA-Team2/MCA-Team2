package com.jwpyo.soundmind

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jwpyo.soundmind.di.persistenceModule
import com.jwpyo.soundmind.di.repositoryModule
import com.jwpyo.soundmind.di.utilsModule
import com.jwpyo.soundmind.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(persistenceModule)
            modules(repositoryModule)
            modules(viewModelModule)
            modules(utilsModule)
        }

        // Initialize the timezone information
        AndroidThreeTen.init(this)
    }
}