package com.example.playlistmaker.search.data.repositoryImpl

import com.example.playlistmaker.common.data.NetworkClient
import com.example.playlistmaker.common.data.mapper.TrackMapper
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.repository.ApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiRepositoryImpl(private val networkClient: NetworkClient) : ApiRepository {

    override fun searchTracksFlow(expression: String): Flow<ConsumerData<List<Track>>> = flow {

        val tracksSearchResponse = networkClient.doRequestSuspend(TracksSearchRequest(expression))
        var resultCode = tracksSearchResponse.resultCode
        val result = if (tracksSearchResponse is TracksSearchResponse) TrackMapper.mapToDomainModel(
            tracksSearchResponse.results
        ) else emptyList()
        if (result.isEmpty() && resultCode == 200) {
            resultCode = 0
        }
        emit(ConsumerData(result, resultCode))
    }
}
