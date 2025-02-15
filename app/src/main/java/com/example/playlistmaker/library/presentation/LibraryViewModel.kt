package com.example.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LibraryViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PagerState>()

init {
    stateLiveData.value = PagerState.entries.find { it.number == 0 }
}
    fun observeState(): LiveData<PagerState> = stateLiveData

    fun saveTab(position: Int) {
        stateLiveData.value = PagerState.entries.find { it.number == position }
    }
}