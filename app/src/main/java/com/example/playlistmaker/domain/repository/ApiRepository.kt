package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.Track

interface ApiRepository {

    fun searchTracks(expression: String): ConsumerData<List<Track>>
}
