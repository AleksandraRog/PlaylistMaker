package com.example.playlistmaker.common.di


import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.library.presentation.GetValueLibraryString
import com.example.playlistmaker.new_playlist.data.file.GetValueFileString
import com.example.playlistmaker.player.domain.GetValueToastString
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val experimentKoinModule = module {

    single<ExecutorService> { Executors.newCachedThreadPool() }

    single<Context> { androidApplication() }

    single<GetValueLibraryString> {
        object : GetValueLibraryString {
            override val playlists: String = androidContext().getString(R.string.playlists)
            override val favoriteTracks: String = androidContext().getString(R.string.favorite_tracks)
        }
    }

    single<GetValueFileString> {
        object : GetValueFileString {
            override val fileNameTemplate: String = androidContext().getString(R.string.file_name_template)
            override val directoryName: String = androidContext().getString(R.string.my_album)
        }
    }

    single<GetValueToastString> {
        object : GetValueToastString {
            override val positiveMessage= androidContext().getString(R.string.add_track_to_playlist)
            override val negativeMessage= androidContext().getString(R.string.double_add_track_to_playlist)
        }
    }
}
