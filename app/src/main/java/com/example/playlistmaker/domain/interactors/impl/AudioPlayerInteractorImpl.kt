package com.example.playlistmaker.domain.interactors.impl

import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor.PlayerStateConsumer
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.repsitory.PlayerRepository
import org.koin.java.KoinJavaComponent.getKoin
import java.util.concurrent.ExecutorService

class AudioPlayerInteractorImpl(
    private val repository: PlayerRepository): AudioPlayerInteractor {

    private val executor: ExecutorService by lazy { getKoin().get<ExecutorService>() }


    override fun play(consumer: PlayerStateConsumer) {
        executor.execute {
            consumer.consume(repository.playPlayer())
        }
    }

    override fun stop(consumer: PlayerStateConsumer) {
        executor.execute {
            consumer.consume(repository.pausePlayer())
        }
    }

    override fun prepare(
        url: String,
        prepareConsumer: PlayerStateConsumer,
        completionConsumer: PlayerStateConsumer
    ) {
        executor.execute {
            repository.preparePlayer(
                url,
                prepareListenerConsumer = object : ListenerConsumer<PlayerState> {
                    override fun consume(data: ConsumerData<PlayerState>) {
                        prepareConsumer.consume(data)
                    }

                },
                completionListenerConsumer = object : ListenerConsumer<PlayerState> {
                    override fun consume(data: ConsumerData<PlayerState>) {
                        completionConsumer.consume(data)
                    }
                })
        }
    }
}
