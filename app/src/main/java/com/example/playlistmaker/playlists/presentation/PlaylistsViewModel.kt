package com.example.playlistmaker.playlists.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.playlists.domain.interactors.PlaylistsInteractor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.LinkedList

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<ListUiState<ItemPlaylistWrapper>>()
    private val previousStateLiveData = MutableLiveData<ListUiState<ItemPlaylistWrapper>>()
    private var historyPlaylists = LinkedList<ItemPlaylistWrapper>()

    init {
        stateLiveData.value = ListUiState.Loading as ListUiState<ItemPlaylistWrapper>
        loadHistory()

    }

    val observeState = MediatorLiveData<ListUiState<ItemPlaylistWrapper>>().apply {
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
            playlistsInteractor.loadPlaylistsFlow()
                .collectLatest { pair ->
                    historyPlaylists = pair.first.mapTo(LinkedList<ItemPlaylistWrapper>()) { ItemPlaylistWrapper.PlaylistSingle(it)  }
                    val tracksState =
                        if (historyPlaylists.size != 0) ListUiState.Content(historyPlaylists) else ListUiState.Empty
                    stateLiveData.postValue(tracksState as ListUiState<ItemPlaylistWrapper>)
                }
        }
    }

    fun del(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.del(playlist)
                .collect { pair ->
                }
        }
    }
}
