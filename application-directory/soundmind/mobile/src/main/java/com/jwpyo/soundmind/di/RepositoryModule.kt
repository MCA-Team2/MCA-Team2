package com.jwpyo.soundmind.di

import com.jwpyo.soundmind.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single<VoiceRepository> { VoiceRepositoryImpl(get()) }
    single<StressRepository> { StressRepositoryImpl(get()) }
}
