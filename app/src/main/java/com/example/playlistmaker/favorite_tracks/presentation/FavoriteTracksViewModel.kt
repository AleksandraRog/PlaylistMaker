package com.example.playlistmaker.favorite_tracks.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.favorite_tracks.domain.interactors.FavoriteTracksInteractor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.LinkedList

class FavoriteTracksViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<ListUiState<Track>>()
    private val previousStateLiveData = MutableLiveData<ListUiState<Track>>()
    private var historyTracks = LinkedList<Track>()

    init {
        stateLiveData.value = ListUiState.Loading as ListUiState<Track>
         loadHistory()
    }

    val observeState = MediatorLiveData<ListUiState<Track>>().apply {
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
                        if (historyTracks.size != 0) ListUiState.Content(historyTracks) else ListUiState.Empty
                    stateLiveData.postValue(tracksState as ListUiState<Track>)
                }
        }
    }

    fun showTrack(track: Track) {
       stateLiveData.postValue(ListUiState.AnyItem(track.trackId) as ListUiState<Track>)
    }
}
