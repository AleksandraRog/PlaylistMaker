package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import retrofit2.Retrofit
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val itunsBaseUrl = "https://itunes.apple.com"

    private val retrofit: Retrofit by lazy {
        getKoin().get<Retrofit> { parametersOf(itunsBaseUrl) }
    }

    private val iTunsService = retrofit.create(RetrofitTrackApi::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (dto is TracksSearchRequest) {
                val resp = iTunsService.search(dto.expression).execute()
                val responseBody = resp.body() ?: Response()
                return responseBody.apply { resultCode = resp.code() }
            } else {
                return Response().apply { resultCode = -100 }
            }
        } catch (e: IOException) {
            return Response().apply { resultCode = -1 }
        }
    }
}
