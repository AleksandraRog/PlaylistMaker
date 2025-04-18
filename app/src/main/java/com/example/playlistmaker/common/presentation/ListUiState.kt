package com.example.playlistmaker.common.presentation

interface ListUiState<T> {

    data object Loading : ListUiState<Nothing>

    data object Empty : ListUiState<Nothing>

    data object Default : ListUiState<Nothing>

    data class Content<T>(val contentList: List<T>) : ListUiState<T>

    data class AnyItem(val itemId: Int) : ListUiState<Nothing>

    interface ListUiIncludeState<T> : ListUiState<T>
}
