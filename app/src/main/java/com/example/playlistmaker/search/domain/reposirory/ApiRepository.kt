package com.example.playlistmaker.search.domain.reposirory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track


interface ApiRepository {

    fun searchTracks(expression: String): ConsumerData<List<Track>>
}
