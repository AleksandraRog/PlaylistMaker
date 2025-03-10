package com.example.playlistmaker.settings.data.local

import com.example.playlistmaker.common.data.SharedPreferencesClient
import com.example.playlistmaker.common.data.dto.Response
import com.example.playlistmaker.settings.data.dto.DarkThemeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DarkThemeClient(private val darkThemeManager: DarkThemeManager) : SharedPreferencesClient {

    override suspend fun doRequestSuspend(): Response {

        return withContext(Dispatchers.IO) {
            try {
                val resp = darkThemeManager.getData()
                if (resp == null) Response().apply { resultCode = -1 }
                else DarkThemeResponse(resp)
            } catch (e: Throwable) {
                Response().apply { resultCode = -1 }
            }
        }

    }
}
