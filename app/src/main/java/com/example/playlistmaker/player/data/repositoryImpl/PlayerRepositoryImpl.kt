package com.example.playlistmaker.player.data.repositoryImpl


import android.media.MediaPlayer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.consumer.ListenerConsumer
import com.example.playlistmaker.player.domain.reposirory.PlayerRepository
import com.example.playlistmaker.player.presentation.model.PlayerState

class PlayerRepositoryImpl(private val mediaPlayer : MediaPlayer) : PlayerRepository {

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

    override fun releasePlayer(): ConsumerData<PlayerState> {
        mediaPlayer.release()
        return ConsumerData(PlayerState.STATE_DEFAULT, 0)
    }

    override fun currentPosition(): ConsumerData<Long> {

        return if (mediaPlayer.currentPosition in 1 until mediaPlayer.duration) ConsumerData( mediaPlayer.currentPosition.toLong(), 0) else ConsumerData( 0.toLong(), 0)
    }
}
