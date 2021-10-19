package com.jwpyo.soundmind.di

import com.jwpyo.soundmind.utils.AppState
import org.koin.dsl.module

val utilsModule = module {
    single<AppState> { AppState() }
}