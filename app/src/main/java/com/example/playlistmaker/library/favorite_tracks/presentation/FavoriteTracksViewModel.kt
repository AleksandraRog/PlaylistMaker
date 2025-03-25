package com.example.playlistmaker.library.favorite_tracks.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.UiState
import com.example.playlistmaker.library.favorite_tracks.domain.interactors.FavoriteTracksInteractor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.LinkedList

class FavoriteTracksViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<UiState>()
    private val previousStateLiveData = MutableLiveData<UiState>()
    private var historyTracks = LinkedList<Track>()

    init {
        stateLiveData.value = UiState.Loading
         loadHistory()
    }

    val observeState = MediatorLiveData<UiState>().apply {
        addSource(stateLiveData) { newValue ->
            previousStateLiveData.value = this.value
            this.value = newValue
        }
    }

    fun restoreState() {
        stateLiveData.postValue(previousStateLiveData.value)
    }

     private fun loadHistory() {
        viewModelScope.launch {
            favoriteTracksInteractor.loadTracksFlow()
                .collectLatest { pair ->
                    historyTracks = pair.first
                    val tracksState =
                        if (historyTracks.size != 0) UiState.Content(historyTracks) else UiState.Empty
                    stateLiveData.postValue(tracksState)
                }
        }
    }

    fun showTrack(track: Track) {
       stateLiveData.postValue(UiState.AnyTrack(track.trackId))
    }
}