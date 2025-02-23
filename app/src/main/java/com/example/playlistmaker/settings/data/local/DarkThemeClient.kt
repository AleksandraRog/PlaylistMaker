package com.example.playlistmaker.settings.data.local

import com.example.playlistmaker.common.data.SharedPreferencesClient
import com.example.playlistmaker.settings.data.dto.DarkThemeResponse
import com.example.playlistmaker.common.data.dto.Response
import java.io.IOException

class DarkThemeClient(private val darkThemeManager: DarkThemeManager) : SharedPreferencesClient {

    override fun doRequest(): Response {
        try {
            val resp = darkThemeManager.getData()
            return if (resp == null)
                Response().apply { resultCode = -1 }
            else
                DarkThemeResponse(resp)
        } catch (e: IOException) {
            return Response().apply { resultCode = -1 }
        }
    }
}
