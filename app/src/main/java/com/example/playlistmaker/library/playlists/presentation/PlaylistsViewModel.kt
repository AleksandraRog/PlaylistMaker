package com.example.playlistmaker.library.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<Boolean>()

    init {
        stateLiveData.value = false
    }

    fun observeState(): LiveData<Boolean> = stateLiveData
}