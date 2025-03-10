package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.usecases.GetTrackByIdUseCase
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerViewModel(
    trackId: Int, getTrackByIdUseCase: GetTrackByIdUseCase,
    private val audioPlayerInteractor: AudioPlayerInteractor,
) : ViewModel() {

    private lateinit var playerPropertyState: PlayerPropertyState
    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private var playStatusLiveData = MutableLiveData<PlayerPropertyState>()
    private var timerJob: Job? = null
    init {
        viewModelScope.launch {
            getTrackByIdUseCase.loadTrackFlow(trackId)
                .collectLatest { pair ->
                    playerPropertyState = PlayerPropertyState(pair.first)
                    screenStateLiveData.postValue(
                        TrackScreenState.Content(pair.first)
                    )
                    playStatusLiveData.postValue(playerPropertyState)
                    preparePlayer(playerPropertyState.track.previewUrl)
                }
        }
    }

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayerPropertyState> = playStatusLiveData

    override fun onCleared() {
        releasePlayer()
    }

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
}
