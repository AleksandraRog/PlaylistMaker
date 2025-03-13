package com.example.playlistmaker.search.domain.reposirory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface HistoryRepository {

    fun registrationDiff(queue: LinkedList<Track>): Flow<Boolean>

    fun loadHistoryFlow(): Flow<ConsumerData<LinkedList<Track>>>

    fun getTrackByIdFlow(trackId : Int) : Flow<ConsumerData<Track>>
}
