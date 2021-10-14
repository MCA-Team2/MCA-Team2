package com.jwpyo.soundmind.di

import com.jwpyo.soundmind.utils.SoundPlayer
import org.koin.dsl.module

val utilsModule = module {
    single<SoundPlayer> { SoundPlayer() }
}