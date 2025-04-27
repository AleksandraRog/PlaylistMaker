package com.example.playlistmaker.common.di

import android.os.Environment
import com.example.playlistmaker.common.data.repositoryImpl.DbElementKeyRepositoryImpl
import com.example.playlistmaker.common.data.repositoryImpl.DbPlaylistTableRepositoryImpl
import com.example.playlistmaker.common.data.repositoryImpl.DbTrackTableRepositoryImpl
import com.example.playlistmaker.common.data.repositoryImpl.ExternalNavigatorImpl
import com.example.playlistmaker.common.domain.repsitory.DbElementKeyRepository
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
import com.example.playlistmaker.common.domain.repsitory.DbTrackTableRepository
import com.example.playlistmaker.common.domain.repsitory.ExternalNavigator
import com.example.playlistmaker.favorite_tracks.data.repositoryImpl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.favorite_tracks.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.new_playlist.data.repsitory.NewPlaylistRepositoryImpl
import com.example.playlistmaker.new_playlist.domain.repository.NewPlaylistRepository
import com.example.playlistmaker.player.data.repositoryImpl.PlayerRepositoryImpl
import com.example.playlistmaker.player.data.repositoryImpl.TrackRepositoryImpl
import com.example.playlistmaker.player.domain.reposirory.PlayerRepository
import com.example.playlistmaker.player.domain.reposirory.TrackRepository
import com.example.playlistmaker.playlist.data.repositoryImpl.PlaylistExternalNavigatorImpl
import com.example.playlistmaker.playlist.data.repositoryImpl.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.domain.repository.PlaylistExternalNavigator
import com.example.playlistmaker.playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.playlists.data.repositoryImpl.PlaylistsRepositoryImpl
import com.example.playlistmaker.playlists.domain.repository.PlaylistsRepository
import com.example.playlistmaker.search.data.repositoryImpl.ApiRepositoryImpl
import com.example.playlistmaker.search.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.search.domain.repository.ApiRepository
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.settings.data.repsitoryImpl.DarkThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import com.example.playlistmaker.sharing.data.repositoryImpl.SharingExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.repsitory.SharingExternalNavigator
import org.koin.android.ext.koin.androidApplication
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

    single<SharingExternalNavigator> {
        SharingExternalNavigatorImpl(get(),)
    }

    single<PlaylistExternalNavigator> {
        PlaylistExternalNavigatorImpl(get(),)
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    single<DbElementKeyRepository> {
        DbElementKeyRepositoryImpl(get(),get(),get(),)
    }

    single<DbTrackTableRepository> {
        DbTrackTableRepositoryImpl(get(),)
    }

    single<DbPlaylistTableRepository> {
        DbPlaylistTableRepositoryImpl(get(),)
    }

    single<NewPlaylistRepository> {
        val externalFilesDir = androidApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        NewPlaylistRepositoryImpl(get(),get(),externalFilesDir, get(),)
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get(),get(),get(),)
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(),)
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(),)
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(),get(),get(),)
    }
}
