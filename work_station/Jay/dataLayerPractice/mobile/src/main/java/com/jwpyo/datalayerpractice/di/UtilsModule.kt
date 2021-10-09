package com.jwpyo.datalayerpractice.di

import com.jwpyo.datalayerpractice.utils.SoundPlayer
import org.koin.dsl.module

val utilsModule = module {
    single<SoundPlayer> { SoundPlayer() }
}