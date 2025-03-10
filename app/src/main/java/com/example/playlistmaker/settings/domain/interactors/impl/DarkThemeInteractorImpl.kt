package com.example.playlistmaker.settings.domain.interactors.impl

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import kotlinx.coroutines.flow.Flow

class DarkThemeInteractorImpl (private val darkThemeRepository : DarkThemeRepository) :
    DarkThemeInteractor {

    override fun getDarkThemeFlow(): Flow<ConsumerData<Boolean>> {
        return darkThemeRepository.getDarkThemeFlow()

    }

    override suspend fun saveDarkTheme(darkTheme: Boolean) {
        darkThemeRepository.saveDarkTheme(darkTheme) }

    override suspend fun getThemeSync(): ConsumerData<Boolean> {
        return darkThemeRepository.getDarkTheme()
    }
}



