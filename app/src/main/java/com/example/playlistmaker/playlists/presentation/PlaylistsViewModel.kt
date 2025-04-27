package com.example.playlistmaker.playlists.presentation

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.presentation.ActionViewModel
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.playlists.domain.interactors.PlaylistsInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.LinkedList

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor
) : ActionViewModel<ItemPlaylistWrapper>() {

    private var historyPlaylists = LinkedList<ItemPlaylistWrapper>()
    private var clickJob: Job? = null
    var current = true

    init {
        screenStateLiveData.value = ListUiState.Loading as ListUiState<ItemPlaylistWrapper>
        loadHistory()

    }

    private fun loadHistory() {
        viewModelScope.launch {
            playlistsInteractor.loadPlaylistsFlow()
                .collectLatest { pair ->
                    historyPlaylists = pair.first.mapTo(LinkedList<ItemPlaylistWrapper>()) { ItemPlaylistWrapper.PlaylistSingle(it)  }
                    val tracksState =
                        if (historyPlaylists.size != 0) ListUiState.Content(historyPlaylists) else ListUiState.Empty
                    screenStateLiveData.postValue(tracksState as ListUiState<ItemPlaylistWrapper>)
                }
        }
    }

    fun showPlaylist(playlist: ItemPlaylistWrapper) {
        showAction(playlist)
    }

    override fun onClickDebounce(item: ItemPlaylistWrapper) {
        screenStateLiveData.postValue(ListUiState.AnyItem(item.playlist.playlistId) as ListUiState<ItemPlaylistWrapper>)
    }



}
