package com.example.playlistmaker.data.repsitoryImpl

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.ApiRepository

class ApiRepositoryImpl(private val networkClient: NetworkClient) : ApiRepository {

    override fun searchTracks(expression: String): ConsumerData<List<Track>> {

        val tracksSearchResponse = networkClient.doRequest(TracksSearchRequest(expression))
        var resultCode = tracksSearchResponse.resultCode
        val result = if (tracksSearchResponse is TracksSearchResponse) TrackMapper.mapToDomainModel(tracksSearchResponse.results) else emptyList()
        if (result.isEmpty() && resultCode == 200 ) {
            resultCode = 0
        }
        return ConsumerData(result, resultCode)
    }
}
