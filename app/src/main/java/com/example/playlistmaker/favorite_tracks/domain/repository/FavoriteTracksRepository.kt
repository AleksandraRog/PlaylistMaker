package com.example.playlistmaker.favorite_tracks.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface FavoriteTracksRepository {

    fun loadHistoryFlow(): Flow<ConsumerData<LinkedList<Track>>>
}
