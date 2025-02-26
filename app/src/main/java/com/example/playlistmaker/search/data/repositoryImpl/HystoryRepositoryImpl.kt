package com.example.playlistmaker.search.data.repositoryImpl

import com.example.playlistmaker.common.data.mapper.TrackMapper
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.data.local.HistoryTrackManager
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import java.util.LinkedList

class HistoryRepositoryImpl(private val historyManager: HistoryTrackManager) :
    HistoryRepository {

    override fun registrationDiff(queue: LinkedList<Track>) {
        historyManager.saveData(TrackMapper.mapToDto(queue))
    }

    override fun loadHistory(): ConsumerData<LinkedList<Track>> {

        return ConsumerData(TrackMapper.mapToDomainModel(historyManager.getData()))

    }

    override fun getTrackById(trackId: Int): ConsumerData<Track> {

        val resalt = loadHistory().result.find { it.trackId == trackId }

        return if (resalt== null) {
            getTrackById(trackId)
        } else {
            ConsumerData(resalt)
        }
    }
}
