package com.example.playlistmaker

import com.example.playlistmaker.data.SharedPreferencesClient
import com.example.playlistmaker.data.local.DarkThemeClient
import com.example.playlistmaker.data.local.DarkThemeManager
import com.example.playlistmaker.data.local.HistoryTrackManager
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repsitoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.data.repsitoryImpl.PlayerRepositoryImpl
import com.example.playlistmaker.data.repsitoryImpl.ApiRepositoryImpl
import com.example.playlistmaker.data.repsitoryImpl.DarkThemeRepositoryImpl
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.domain.interactors.HistoryInteractor
import com.example.playlistmaker.domain.interactors.TracksInteractor
import com.example.playlistmaker.domain.interactors.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.interactors.impl.DarkThemeInteractorImpl
import com.example.playlistmaker.domain.interactors.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.interactors.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.ApiRepository
import com.example.playlistmaker.domain.repository.DarkThemeRepository
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repsitory.PlayerRepository
import com.example.playlistmaker.domain.usecases.GetTrackByIdUseCase
import com.example.playlistmaker.domain.usecases.SaveHistoryUseCase
import com.example.playlistmaker.domain.usecases.UpdateHistoryQueueUseCase

object Creator {

    // Api
    fun provideTracksInteractor(): TracksInteractor {
           return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksRepository(): ApiRepository {
        return ApiRepositoryImpl(RetrofitNetworkClient())
    }

    //History
    private fun getHistoryRepository(): HistoryRepository {

        return HistoryRepositoryImpl(HistoryTrackManager())
    }

    fun provideSaveHistoryUseCase(): SaveHistoryUseCase {

        return SaveHistoryUseCase(getHistoryRepository())
    }

    fun provideHistoryInteractor(): HistoryInteractor {

        return HistoryInteractorImpl(getHistoryRepository())
    }

    //DarkTheme
    private fun getDarkThemeRepository(): DarkThemeRepository {
       return DarkThemeRepositoryImpl(DarkThemeClient())
    }

    fun provideDarkThemeInteractor(): DarkThemeInteractor {
       return DarkThemeInteractorImpl(getDarkThemeRepository())
    }

    //PLAYER
    private fun getAudioPlayerRepository(): PlayerRepository {
       return PlayerRepositoryImpl()
   }

    fun provideAudioPlayerInteractor() : AudioPlayerInteractor {

        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }

   //
    fun provideGetTrackByIdUseCase(): GetTrackByIdUseCase {
            return GetTrackByIdUseCase(getHistoryRepository())
    }

    fun provideUpdateHistoryQueueUseCase() : UpdateHistoryQueueUseCase {
        return UpdateHistoryQueueUseCase()
    }
}
