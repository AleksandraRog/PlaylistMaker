package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}
