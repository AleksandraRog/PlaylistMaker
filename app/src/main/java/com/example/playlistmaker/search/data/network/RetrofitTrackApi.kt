package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitTrackApi {

    @GET("/search?entity=song")
    suspend fun searchSuspend(@Query("term") text: String) : TracksSearchResponse
}
