package com.example.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var screenStateLiveData = MutableLiveData<Boolean>()

    fun getScreenStateLiveData(): LiveData<Boolean> = screenStateLiveData

    fun focusChange(screen: Boolean) {
        screenStateLiveData.value = screen
    }
}
