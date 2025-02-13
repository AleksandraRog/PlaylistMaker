package com.example.playlistmaker.player.domain.interactors

import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.player.presentation.model.PlayerState

interface AudioPlayerInteractor {

    fun play(consumer: PlayerStateConsumer)

    fun stop(consumer: PlayerStateConsumer)

    fun prepare(url: String,
                prepareConsumer: PlayerStateConsumer,
                completionConsumer: PlayerStateConsumer
    )

    interface PlayerStateConsumer : Consumer<PlayerState>
}
