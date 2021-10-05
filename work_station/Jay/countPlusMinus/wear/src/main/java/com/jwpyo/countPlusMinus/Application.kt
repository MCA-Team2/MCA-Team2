package com.jwpyo.countPlusMinus

import android.app.Application
import com.jwpyo.countPlusMinus.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(viewModelModule)
        }
    }
}