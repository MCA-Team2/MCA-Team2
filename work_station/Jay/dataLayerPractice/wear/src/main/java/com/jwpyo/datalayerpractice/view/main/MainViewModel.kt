package com.jwpyo.datalayerpractice.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel() : ViewModel() {
    val count = MutableLiveData<Int>()
}