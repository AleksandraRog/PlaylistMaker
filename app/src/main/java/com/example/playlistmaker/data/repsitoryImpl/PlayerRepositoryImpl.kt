package com.example.playlistmaker.data.repsitoryImpl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.repsitory.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private val mediaPlayer : MediaPlayer by lazy { MediaPlayer() }

    override fun playPlayer(): ConsumerData<PlayerState> {
        mediaPlayer.start()
        return ConsumerData(PlayerState.STATE_PLAYING, 0)
    }

    override fun pausePlayer(): ConsumerData<PlayerState> {
        mediaPlayer.pause()
        return ConsumerData(PlayerState.STATE_PAUSED, 0)
    }

    override fun preparePlayer(url: String, prepareListenerConsumer: ListenerConsumer<PlayerState>, completionListenerConsumer: ListenerConsumer<PlayerState>) {

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            prepareListenerConsumer.consume(ConsumerData(PlayerState.STATE_PREPARED, 0))
        }
        mediaPlayer.setOnCompletionListener{
            completionListenerConsumer.consume(ConsumerData(PlayerState.STATE_PREPARED, 0))
        }
    }
}
