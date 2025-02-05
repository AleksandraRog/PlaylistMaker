package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.model.PlayerState

interface AudioPlayerInteractor {

    fun play(consumer: PlayerStateConsumer)

    fun stop(consumer: PlayerStateConsumer)

    fun prepare( url: String,
                 prepareConsumer: PlayerStateConsumer,
                 completionConsumer: PlayerStateConsumer)

    interface PlayerStateConsumer : Consumer<PlayerState>
}
