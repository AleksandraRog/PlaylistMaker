package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.model.Track
import java.util.LinkedList

interface HistoryInteractor {

    fun loadTracks(consumer: HistoryTracksConsumer)

    interface HistoryTracksConsumer : Consumer<LinkedList<Track>>
}
