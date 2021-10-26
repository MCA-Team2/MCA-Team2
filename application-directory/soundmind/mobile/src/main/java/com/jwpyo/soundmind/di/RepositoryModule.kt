package com.jwpyo.soundmind.di

import com.jwpyo.soundmind.repository.PPGRepository
import com.jwpyo.soundmind.repository.PPGRepositoryImpl
import com.jwpyo.soundmind.repository.VoiceRepository
import com.jwpyo.soundmind.repository.VoiceRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<VoiceRepository> { VoiceRepositoryImpl(get()) }
    single<PPGRepository> { PPGRepositoryImpl(get()) }
}
