package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.Track
import java.util.LinkedList

interface HistoryRepository {

    fun registrationDiff(queue: LinkedList<Track>)

    fun loadHistory() : ConsumerData<LinkedList<Track>>

    fun getTrackById(trackId : Int) : ConsumerData<Track>
}
