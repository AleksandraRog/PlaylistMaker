package com.example.playlistmaker.search.data.repositoryImpl

import com.example.playlistmaker.common.data.mapper.TrackMapper
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.data.local.HistoryTrackManager
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.LinkedList

class HistoryRepositoryImpl(private val historyManager: HistoryTrackManager) :
    HistoryRepository {

    private val _historyFlow = MutableStateFlow(ConsumerData(LinkedList<Track>()))
    private val historyFlow: StateFlow<ConsumerData<LinkedList<Track>>> = _historyFlow.asStateFlow()

    override fun registrationDiff(queue: LinkedList<Track>) : Flow<Boolean> = flow{

        val reg = historyManager.saveDataSuspend(TrackMapper.mapToPreferencesDto(queue))
        _historyFlow.value = ConsumerData(TrackMapper.mapToDomainModel(historyManager.getDataSuspend()))
        emit(reg)
    }

    override fun loadHistoryFlow(): Flow<ConsumerData<LinkedList<Track>>> = flow {
        val consumerData = ConsumerData(TrackMapper.mapToDomainModel(historyManager.getDataSuspend()))
        _historyFlow.value = consumerData
        emit(consumerData)
    }

     override fun getTrackByIdFlow(trackId: Int): Flow<ConsumerData<Track>> = flow {

         val track = historyFlow
            .mapNotNull { consumerData ->
                consumerData.result.find { it.trackId == trackId }?.let { ConsumerData(it) }
            }
            .first()
         emit(track)
    }
}
