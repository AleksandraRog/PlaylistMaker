package com.example.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var screenStateLiveData = MutableLiveData<Screens>()

    fun getScreenStateLiveData(): LiveData<Screens> = screenStateLiveData

    fun navigateTo(screen: Screens) {
        screenStateLiveData.value = screen
    }
}
