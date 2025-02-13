package com.example.playlistmaker.search.data.repositoryImpl

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.common.data.mapper.TrackMapper
import com.example.playlistmaker.common.data.NetworkClient
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.reposirory.ApiRepository

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
