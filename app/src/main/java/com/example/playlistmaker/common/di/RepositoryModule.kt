package com.example.playlistmaker.common.di

import com.example.playlistmaker.player.data.repositoryImpl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.reposirory.PlayerRepository
import com.example.playlistmaker.search.data.repositoryImpl.ApiRepositoryImpl
import com.example.playlistmaker.search.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.search.domain.reposirory.ApiRepository
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import com.example.playlistmaker.settings.data.repsitoryImpl.DarkThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import com.example.playlistmaker.sharing.data.repositoryImpl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.repsitory.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {

    single<HistoryRepository> {
       HistoryRepositoryImpl(get())
    }

    single<ApiRepository> {
       ApiRepositoryImpl(get())
    }

    single<DarkThemeRepository> {
       DarkThemeRepositoryImpl(get(), get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }
}
