package com.jwpyo.datalayerpractice.di

import com.google.android.gms.wearable.Wearable
import com.jwpyo.datalayerpractice.view.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(Wearable.getDataClient(androidContext())) }
}
