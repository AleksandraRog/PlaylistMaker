package com.example.playlistmaker.common.di

import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.library.favorite_tracks.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.library.favorite_tracks.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.example.playlistmaker.library.playlists.presentation.PlaylistsViewModel
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import com.example.playlistmaker.sharing.domain.interactors.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.presentation.SharingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
       DarkThemeViewModel( androidApplication(),get())
      }

    viewModel {params ->
        val appLink: String = androidContext().getString(R.string.link_course)
        val termsLink: String = androidContext().getString(R.string.link_arrow)
        val emailData = EmailData(androidContext().getString(R.string.support_email),
            androidContext().getString(R.string.theme_support_message),
            androidContext().getString(R.string.text_support_message))

        val sharingInteractor = get<SharingInteractor>().apply {
            getShareAppLink = { appLink }
            getTermsLink = { termsLink }
            getSupportEmailData = { emailData }
        }

       SharingsViewModel(sharingInteractor)
    }

    viewModel {
        SearchViewModel( androidApplication(),get(),get(),get(),)
    }

    viewModel {(trackId: Bundle) ->
        PlayerViewModel(trackId, get(),get(),)
    }

    viewModel {
      MainViewModel()
    }

    viewModel {
        LibraryViewModel()
    }

    viewModel {
        FavoriteTracksViewModel(get<FavoriteTracksInteractor>())
    }

    viewModel {
        PlaylistsViewModel()
    }
}
