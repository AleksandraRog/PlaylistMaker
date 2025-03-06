package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.domain.usecases.GetTrackByIdUseCase
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

        getTrackByIdUseCase.execute(trackId, consumer = object : Consumer<Track> {
            override fun consume(data: ConsumerData<Track>) {
                playerPropertyState = PlayerPropertyState(data.result)
                screenStateLiveData.postValue(
                    TrackScreenState.Content(data.result)
                )

                preparePlayer(playerPropertyState.track.previewUrl)
            }
        })
    }

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayerPropertyState> = playStatusLiveData

    override fun onCleared() {
        releasePlayer()
    }

    private fun preparePlayer(url: String) {

        audioPlayerInteractor.prepare(
            url,
            prepareConsumer = object : AudioPlayerInteractor.PlayerStateConsumer {
                override fun consume(data: ConsumerData<PlayerState>) {
                    playStatusLiveData.postValue(
                        playerPropertyState.apply { playerState = data.result }

                    )

                }
            },
            completionConsumer = object : AudioPlayerInteractor.PlayerStateConsumer {
                override fun consume(data: ConsumerData<PlayerState>) {
                    playStatusLiveData.postValue(
                        playerPropertyState.apply {
                            playerState = data.result
                            timer = 0L
                        }
                    )
                }
            }
        )
    }

    private fun startPlayer() {

        audioPlayerInteractor.play(consumer = object : AudioPlayerInteractor.PlayerStateConsumer {
            override fun consume(data: ConsumerData<PlayerState>) {
                playStatusLiveData.postValue(
                    playerPropertyState.apply { playerState = data.result })
                startTimer()
            }
        })
    }

    fun pausePlayer() {

        audioPlayerInteractor.stop(consumer = object : AudioPlayerInteractor.PlayerStateConsumer {
            override fun consume(data: ConsumerData<PlayerState>) {
                playStatusLiveData.postValue(
                    playerPropertyState.apply { playerState = data.result })
            }
        })
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
        audioPlayerInteractor.release(consumer = object : AudioPlayerInteractor.PlayerStateConsumer {
            override fun consume(data: ConsumerData<PlayerState>) {
                playStatusLiveData.postValue(
                    playerPropertyState.apply { playerState = data.result })
            }
        })
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
