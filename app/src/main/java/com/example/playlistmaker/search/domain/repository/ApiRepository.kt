package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface ApiRepository {
   fun searchTracksFlow(expression: String): Flow<ConsumerData<List<Track>>>
}
