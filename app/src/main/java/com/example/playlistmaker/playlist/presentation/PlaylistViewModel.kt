package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ActionViewModel
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.SharingObjects
import com.example.playlistmaker.playlist.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistViewModel(
    playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    ) : ActionViewModel<Track>() {

    private var playlist: Playlist? = null
    fun getPreviousScreenStateLiveData(): LiveData<ListUiState<Track>> = previousScreenStateLiveData

    init {
        screenStateLiveData.value = ListUiState.Loading as ListUiState<Track>
        viewModelScope.launch {
            playlistInteractor.loadPlaylistFlow(playlistId)
                .collectLatest { pair ->
                    if (pair.first != null) {
                        playlist = pair.first
                        screenStateLiveData.postValue(
                            PlaylistUiState.LoadPlaylist(pair.first!!) as ListUiState<Track>)

                        delay(50)
                        if(pair.second?.isNotEmpty() == true) {
                            screenStateLiveData.postValue(
                            ListUiState.Content(pair.second!!) as ListUiState<Track>)
                        } else {
                            screenStateLiveData.postValue(ListUiState.Empty as ListUiState<Track>)
                        }
                    }
                }

        }
    }

    fun getIntentProperty() {
        if (playlist?.playlistSize == 0) {
            screenStateLiveData.postValue(PlaylistUiState.ShowToastEmpty as ListUiState<Track>)
        } else {
            viewModelScope.launch {
                playlistInteractor.getIntentProperty(SharingObjects.SHARE_PLAYLIST, playlist!!)
                    .collect { sharingObj ->
                        screenStateLiveData.postValue(
                            PlaylistUiState.SharePlaylist(sharingObj) as ListUiState<Track>
                        )
                    }
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist!!)
                .collect{ ok ->
                    screenStateLiveData.postValue(PlaylistUiState.DeletePlaylist as ListUiState<Track>
                    )
                }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track, playlist!!)
                .collect{ ok ->
                    screenStateLiveData.postValue(PlaylistUiState.DeleteTrack as ListUiState<Track>
                    )
                }
        }
    }

    fun showBottomSheetMenu() {
        screenStateLiveData.postValue(PlaylistUiState.ExpandBottomSheetMenu(playlist!!) as ListUiState<Track>)
    }

    fun prepareActionToEditPlaylist() {
        screenStateLiveData.postValue(PlaylistUiState.EditPlaylist(playlist!!))
    }

    override fun onClickDebounce(item: Track) {
        screenStateLiveData.postValue(ListUiState.AnyItem(item.trackId) as ListUiState<Track>)
    }
}
