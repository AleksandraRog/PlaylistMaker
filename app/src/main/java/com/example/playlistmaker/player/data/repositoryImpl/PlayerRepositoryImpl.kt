package com.example.playlistmaker.player.data.repositoryImpl


import android.media.MediaPlayer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.player.domain.reposirory.PlayerRepository
import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl(private val mediaPlayer : MediaPlayer) : PlayerRepository {

    override fun currentPosition(): ConsumerData<Long> {

        return if (mediaPlayer.currentPosition in 1 until mediaPlayer.duration) ConsumerData( mediaPlayer.currentPosition.toLong(), 0) else ConsumerData( 0.toLong(), 0)
    }

    override fun preparePlayerFlow(url: String): Flow<PlayerState> = callbackFlow {
        withContext(Dispatchers.IO) {
            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                trySend(PlayerState.STATE_DEFAULT).isSuccess
                close(e)
                return@withContext
            }
        }
        withContext(Dispatchers.Main) {
            mediaPlayer.setOnPreparedListener {
                trySend(PlayerState.STATE_PREPARED).isSuccess
            }
            mediaPlayer.setOnCompletionListener {
                trySend(PlayerState.STATE_PREPARED).isSuccess
            }
        }
        awaitClose {
            mediaPlayer.setOnPreparedListener(null)
            mediaPlayer.setOnCompletionListener(null)
        }
    }

    override fun playPlayerFlow(): Flow<PlayerState> = flow {
        withContext(Dispatchers.Main) {
            mediaPlayer.start()
        }
        emit(PlayerState.STATE_PLAYING)
    }

    override fun pausePlayerFlow(): Flow<PlayerState> = flow {
        withContext(Dispatchers.IO) {
            mediaPlayer.pause()
        }
        emit(PlayerState.STATE_PAUSED)
    }

    override fun releasePlayerFlow(): Flow<PlayerState> = flow {
        withContext(Dispatchers.Main) {
            mediaPlayer.release()
        }
        emit(PlayerState.STATE_DEFAULT)
    }
}
