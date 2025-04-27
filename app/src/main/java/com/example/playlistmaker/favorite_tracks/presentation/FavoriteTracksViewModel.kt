package com.example.playlistmaker.favorite_tracks.presentation

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ActionViewModel
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.favorite_tracks.domain.interactors.FavoriteTracksInteractor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.LinkedList

class FavoriteTracksViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ActionViewModel<Track>() {

    private var historyTracks = LinkedList<Track>()

    init {
        screenStateLiveData.value = ListUiState.Loading as ListUiState<Track>
         loadHistory()
    }

     private fun loadHistory() {
        viewModelScope.launch {
            favoriteTracksInteractor.loadTracksFlow()
                .collectLatest { pair ->
                    historyTracks = pair.first
                    val tracksState =
                        if (historyTracks.size != 0) ListUiState.Content(historyTracks) else ListUiState.Empty
                    screenStateLiveData.postValue(tracksState as ListUiState<Track>)
                }
        }
    }

    fun showTrack(track: Track) {
        showAction(track)
    }

    override fun onClickDebounce(item: Track) {
        screenStateLiveData.postValue(ListUiState.AnyItem(item.trackId) as ListUiState<Track>)

    }
}
