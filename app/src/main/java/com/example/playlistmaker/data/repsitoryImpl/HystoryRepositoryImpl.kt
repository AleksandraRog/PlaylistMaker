package com.example.playlistmaker.data.repsitoryImpl

import com.example.playlistmaker.data.local.HistoryTrackManager
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
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
        return ConsumerData(loadHistory().result.find { it.trackId == trackId } ?: loadHistory().result.last)
    }
}
