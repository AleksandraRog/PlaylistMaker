package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.dto.Response

interface NetworkClient {
    suspend fun doRequestSuspend(dto: Any): Response
}
