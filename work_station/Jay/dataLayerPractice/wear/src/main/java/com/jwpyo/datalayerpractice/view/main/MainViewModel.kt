package com.jwpyo.datalayerpractice.view.main

import androidx.lifecycle.MutableLiveData
import com.jwpyo.datalayerpractice.base.LiveCoroutinesViewModel

class MainViewModel() : LiveCoroutinesViewModel() {
    val count = MutableLiveData<Int>()
}