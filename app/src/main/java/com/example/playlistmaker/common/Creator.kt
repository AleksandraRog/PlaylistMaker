package com.example.playlistmaker.common

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.domain.usecases.GetTrackByIdUseCase
import com.example.playlistmaker.player.data.repositoryImpl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interactors.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.reposirory.PlayerRepository
import com.example.playlistmaker.search.data.local.HistoryTrackManager
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repositoryImpl.ApiRepositoryImpl
import com.example.playlistmaker.search.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.interactors.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactors.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.reposirory.ApiRepository
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import com.example.playlistmaker.search.domain.usecase.SaveHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.UpdateHistoryQueueUseCase
import com.example.playlistmaker.settings.data.local.DarkThemeClient
import com.example.playlistmaker.settings.data.repsitoryImpl.DarkThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactors.impl.DarkThemeInteractorImpl
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import com.example.playlistmaker.sharing.data.repositoryImpl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.interactors.SharingInteractor
import com.example.playlistmaker.sharing.domain.interactors.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.repsitory.ExternalNavigator

object Creator {

    // Api
    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): ApiRepository {
        return ApiRepositoryImpl(RetrofitNetworkClient(context))
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

        return PlayerRepositoryImpl(MediaPlayer())
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

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl()
    }

}