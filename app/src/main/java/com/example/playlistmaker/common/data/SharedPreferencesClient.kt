package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.dto.Response

interface SharedPreferencesClient {
    fun doRequest(): Response
}
