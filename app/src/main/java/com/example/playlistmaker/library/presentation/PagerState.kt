package com.example.playlistmaker.library.presentation


import org.koin.java.KoinJavaComponent.getKoin

enum class PagerState(val number: Int, val titleName: String) {
    LIBRARIES(1,    getKoin().get<GetValueString>().playlists),
    FAVORITE_TRACKS(0,getKoin().get<GetValueString>().favoriteTracks);
}
