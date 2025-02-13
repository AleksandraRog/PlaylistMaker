package com.example.playlistmaker.search.domain.reposirory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import java.util.LinkedList

interface HistoryRepository {

    fun registrationDiff(queue: LinkedList<Track>)

    fun loadHistory() : ConsumerData<LinkedList<Track>>

    fun getTrackById(trackId : Int) : ConsumerData<Track>
}
