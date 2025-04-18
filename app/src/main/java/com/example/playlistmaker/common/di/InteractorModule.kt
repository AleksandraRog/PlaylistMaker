package com.example.playlistmaker.common.di

import com.example.playlistmaker.favorite_tracks.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.favorite_tracks.domain.interactors.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.new_playlist.domain.interactors.MakePlaylistInteractor
import com.example.playlistmaker.new_playlist.domain.interactors.impl.MakePlaylistInteractorImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interactors.TrackInteractor
import com.example.playlistmaker.player.domain.interactors.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.interactors.impl.TrackInteractorImpl
import com.example.playlistmaker.playlists.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.playlists.domain.interactors.impl.PlaylistsInteractorImpl
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
        AudioPlayerInteractorImpl(get(),)
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(get(),get(),get(),)
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get(),)
    }

    single<MakePlaylistInteractor> {
        MakePlaylistInteractorImpl(get(), get(),)
    }
}
