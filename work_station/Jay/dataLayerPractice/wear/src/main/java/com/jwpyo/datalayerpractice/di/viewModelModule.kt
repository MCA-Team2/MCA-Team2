package com.jwpyo.datalayerpractice.di

import com.jwpyo.datalayerpractice.view.main.MainViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val viewModelModule = module {
    viewModel { MainViewModel() }
}
