package com.example.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.domain.usecases.GetTrackByIdUseCase
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.usecase.UpdateTimerTaskUseCase
import com.example.playlistmaker.player.presentation.model.PlayerState

class PlayerViewModel(
    trackId: Int, getTrackByIdUseCase: GetTrackByIdUseCase,
    private val audioPlayerInteractor: AudioPlayerInteractor,
) : ViewModel() {

    private lateinit var playerPropertyState: PlayerPropertyState
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var currentTrackTimeInMillis = 0L
    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private var playStatusLiveData = MutableLiveData<PlayerPropertyState>()

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
        mainThreadHandler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
                    currentTrackTimeInMillis = 0L
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

        val startTime = System.currentTimeMillis()
        val currentTime = currentTrackTimeInMillis
        val timerRunnable = object : Runnable {
            override fun run() {
                if (playerPropertyState.playerState == PlayerState.STATE_PLAYING) {
                    currentTrackTimeInMillis = UpdateTimerTaskUseCase.execute(
                        startTime,
                        currentTime
                    )
                    playStatusLiveData.postValue(
                        playerPropertyState.apply { timer = currentTrackTimeInMillis }
                    )
                    mainThreadHandler.postDelayed(this, DELAY)
                }
            }
        }
        mainThreadHandler.post(timerRunnable)
    }

    companion object {
        private const val DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
