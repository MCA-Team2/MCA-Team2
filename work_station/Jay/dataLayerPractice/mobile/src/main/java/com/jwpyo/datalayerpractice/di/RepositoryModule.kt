package com.jwpyo.datalayerpractice.di

import com.jwpyo.datalayerpractice.repository.VoiceRepository
import com.jwpyo.datalayerpractice.repository.VoiceRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<VoiceRepository> { VoiceRepositoryImpl(get()) }
}
