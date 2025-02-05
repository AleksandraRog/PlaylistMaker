package com.example.playlistmaker.domain.interactors
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.model.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: FindTracksConsumer)

    interface FindTracksConsumer : Consumer<List<Track>>
}
