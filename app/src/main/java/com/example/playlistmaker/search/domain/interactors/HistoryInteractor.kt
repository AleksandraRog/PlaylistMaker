package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.model.Track
import java.util.LinkedList

interface HistoryInteractor {

    fun loadTracks(consumer: HistoryTracksConsumer)

    interface HistoryTracksConsumer : Consumer<LinkedList<Track>>
}
