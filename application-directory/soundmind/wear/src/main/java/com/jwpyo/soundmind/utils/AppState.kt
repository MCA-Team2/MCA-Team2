package com.jwpyo.soundmind.utils

import androidx.lifecycle.MutableLiveData

class AppState {
    val recordState: MutableLiveData<Boolean> = MutableLiveData(false)
}