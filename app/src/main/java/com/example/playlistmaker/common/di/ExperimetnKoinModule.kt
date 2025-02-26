package com.example.playlistmaker.common.di


import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.library.presentation.GetValueString
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val experimetnKoinModule = module {

    single<ExecutorService> { Executors.newCachedThreadPool() }

    single<Context> { androidApplication() }

    single<GetValueString> {
        object : GetValueString {
            override val playlists: String = androidContext().getString(R.string.playlists)
            override val favoriteTracks: String = androidContext().getString(R.string.favorite_tracks)
        }
    }
}