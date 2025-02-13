package com.example.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MainViewModel : ViewModel() {

    private var screenStateLiveData = MutableLiveData<Screens>()

    fun getScreenStateLiveData(): LiveData<Screens> = screenStateLiveData

    fun navigateTo(screen: Screens) {
        screenStateLiveData.value = screen
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel()
            }
        }
    }
}
