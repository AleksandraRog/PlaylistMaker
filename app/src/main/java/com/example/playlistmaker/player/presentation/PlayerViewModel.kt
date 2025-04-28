package com.example.playlistmaker.player.presentation

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interactors.TrackInteractor
import com.example.playlistmaker.common.presentation.mapper.BundleMapper.toModel
import com.example.playlistmaker.player.presentation.model.PlayerPropertyState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.TrackUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.LinkedList

class PlayerViewModel(
    trackIdBundle: Bundle,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val trackInteractor: TrackInteractor,
) : ViewModel() {

    private lateinit var playerPropertyState: PlayerPropertyState
    private var screenStateLiveData = MutableLiveData<ListUiState<ItemPlaylistWrapper.PlaylistPair>>()
    private var playStatusLiveData = MutableLiveData<PlayerPropertyState>()
    private var timerJob: Job? = null
    init {
        screenStateLiveData.value = ListUiState.Loading as ListUiState<ItemPlaylistWrapper.PlaylistPair>
        viewModelScope.launch {
            trackInteractor.loadTrackFlow(trackIdBundle.toModel())
                .collectLatest { pair ->
                    if (pair.first != null) {
                        playerPropertyState = PlayerPropertyState(pair.first!!)
                        screenStateLiveData.postValue(
                            TrackUiState.LoadTrack(pair.first!!)
                        )
                        playStatusLiveData.postValue(playerPropertyState)
                        preparePlayer(playerPropertyState.track.previewUrl)
                    } else screenStateLiveData.postValue(
                        ListUiState.Empty as ListUiState<ItemPlaylistWrapper.PlaylistPair>
                    )
                }
        }
    }

    fun getScreenStateLiveData(): LiveData<ListUiState<ItemPlaylistWrapper.PlaylistPair>> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayerPropertyState> = playStatusLiveData

    override fun onCleared() {
        releasePlayer()
    }

    // player state functions
    private fun preparePlayer(url: String) {
        viewModelScope.launch {
           audioPlayerInteractor.prepareFlow(url)
                .collect { playerState ->
                    if (playerState == PlayerState.STATE_PREPARED) {
                        playStatusLiveData.postValue(
                            playerPropertyState.apply {
                                this@PlayerViewModel.playerPropertyState.playerState =
                                    playerState
                                timer = 0L
                            }
                        )
                    }
                }
        }
    }

    private fun startPlayer() {
        viewModelScope.launch {
            audioPlayerInteractor.playFlow()
                .collect { playerState ->

                    playStatusLiveData.postValue(
                        playerPropertyState.apply {
                            this@PlayerViewModel.playerPropertyState.playerState =
                                playerState
                        }
                    )
                    startTimer()
                }
        }
    }

    fun pausePlayer() {

        viewModelScope.launch {
            audioPlayerInteractor.stopFlow()
                .collect { playerState ->
                    playStatusLiveData.postValue(
                        playerPropertyState.apply {
                            this@PlayerViewModel.playerPropertyState.playerState =
                                playerState
                        }
                    )
                }
        }
    }

    fun playbackControl() {

        when (playerPropertyState.playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> {}
        }
    }

    private fun releasePlayer() {

        viewModelScope.launch {
            audioPlayerInteractor.releaseFlow()
                .collect { playerState ->
                    playStatusLiveData.postValue(
                        playerPropertyState.apply {
                            this@PlayerViewModel.playerPropertyState.playerState =
                                playerState
                        }
                    )
                }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerPropertyState.playerState == PlayerState.STATE_PLAYING) {
                delay(300L)
                playStatusLiveData
                    .postValue(playerPropertyState.apply { timer = audioPlayerInteractor.currentPosition()
                    })
            }
        }
    }

    //track state functions
    fun favoriteControl(isChecked: Boolean) {
        viewModelScope.launch {
            trackInteractor.favoriteControl(this@PlayerViewModel.playerPropertyState.track, isChecked)
                .collect { favoriteState ->
                    playStatusLiveData.postValue(
                        playerPropertyState.apply {
                            this@PlayerViewModel.playerPropertyState.isFavorite =
                                favoriteState
                        }
                    )
                }
        }
    }

    // bottomSheet functions
    fun getPlaylists() {
        viewModelScope.launch {
            trackInteractor.loadPlaylists(playerPropertyState.track)
                .collect { pairList ->
                    val list = pairList.mapTo(LinkedList<ItemPlaylistWrapper.PlaylistPair>()){ ItemPlaylistWrapper.PlaylistPair(it.first, it.second)}
                    screenStateLiveData.postValue(ListUiState.Content(list))
                }
        }
    }

    fun addTrack(playlistPair: ItemPlaylistWrapper.PlaylistPair) {

        viewModelScope.launch {
            trackInteractor.insertTrackToPlaylist(playerPropertyState.track, playlistPair)
                .collect { message ->
                    screenStateLiveData.postValue(TrackUiState.ToastMessage(message))
                }
        }
    }
}
