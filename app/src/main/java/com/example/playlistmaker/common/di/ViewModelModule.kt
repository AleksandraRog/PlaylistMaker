package com.example.playlistmaker.common.di

import com.example.playlistmaker.library.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.example.playlistmaker.library.presentation.PlaylistsViewModel
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import com.example.playlistmaker.sharing.domain.interactors.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.presentation.SharingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
       DarkThemeViewModel( androidApplication(),get())
      }

    viewModel {params ->
        val appLink: String = params.get()
        val termsLink: String = params.get()
        val emailData: EmailData = params.get()

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

    viewModel {(trackId: Int) ->
        PlayerViewModel(trackId, get(),get())
    }

    viewModel {
      MainViewModel()
    }

    viewModel {
        LibraryViewModel()
    }

    viewModel {
        FavoriteTracksViewModel()
    }
    viewModel {
        PlaylistsViewModel()
    }
}
