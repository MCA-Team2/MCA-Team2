package com.jwpyo.datalayerpractice.di

import androidx.room.Room
import com.jwpyo.datalayerpractice.R
import com.jwpyo.datalayerpractice.persistence.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val persistenceModule = module {
    single {
        Room
            .databaseBuilder(
                androidApplication(),
                AppDatabase::class.java,
                androidApplication().getString(R.string.database)
            )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().getVoiceDao() }
}