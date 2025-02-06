package com.example.playlistmaker.data.local

import com.example.playlistmaker.data.SharedPreferencesClient
import com.example.playlistmaker.data.dto.DarkThemeResponse
import com.example.playlistmaker.data.dto.Response
import java.io.IOException

class DarkThemeClient() : SharedPreferencesClient {

    override fun doRequest(): Response {
        try {
            val resp = DarkThemeManager.getData()
            return if (resp == null)
                Response().apply { resultCode = -1 }
            else
                DarkThemeResponse(resp)
        } catch (e: IOException) {
            return Response().apply { resultCode = -1 }
        }
    }
}
