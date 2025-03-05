package com.example.playlistmaker.settings.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData

interface DarkThemeRepository {

    fun getDarkTheme() : ConsumerData<Boolean>

    fun saveDarkTheme(darkTheme: Boolean)
}
