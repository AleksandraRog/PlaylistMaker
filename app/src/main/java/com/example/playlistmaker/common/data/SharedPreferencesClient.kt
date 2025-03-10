package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.dto.Response

interface SharedPreferencesClient {

    suspend fun doRequestSuspend(): Response
}
