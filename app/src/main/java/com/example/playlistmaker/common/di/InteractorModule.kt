package com.example.playlistmaker.common.di

import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interactors.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.interactors.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactors.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactors.impl.DarkThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.interactors.SharingInteractor
import com.example.playlistmaker.sharing.domain.interactors.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    single<DarkThemeInteractor> {
        DarkThemeInteractorImpl(get())
    }

    factory <AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}
