package com.example.playlistmaker.search.domain.interactors
import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.model.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: FindTracksConsumer)

    interface FindTracksConsumer : Consumer<List<Track>>
}
