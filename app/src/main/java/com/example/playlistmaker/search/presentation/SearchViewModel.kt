package com.example.playlistmaker.search.presentation


import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ActionViewModel
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.domain.usecase.UpdateHistoryQueueUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.LinkedList

class SearchViewModel(
    private val historyInteractor: HistoryInteractor,
    private val tracksInteractor: TracksInteractor,
    private val updateHistoryQueueUseCase: UpdateHistoryQueueUseCase
) : ActionViewModel<Track>() {

    private var searchJob: Job? = null
    private var updateHistoryJob: Job? = null


    init {
        screenStateLiveData.value = ListUiState.Default as ListUiState<Track>
    }

    private val tracks = ArrayList<Track>()
    private var historyTracks = HistoryQueue(LinkedList())

    private fun loadHistory() {
        viewModelScope.launch {
            historyInteractor.loadTracksFlow()
                .collect { pair ->
                    historyTracks = HistoryQueue(pair.first)
                                    val trackListUiState =
                    if (historyTracks.size != 0) TrackListUiState.History(historyTracks) else TrackListUiState.EmptyHistory
                    screenStateLiveData.postValue(trackListUiState)
                }
        }
    }

    private fun searchRequest(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
           viewModelScope.launch {
               tracksInteractor.searchTracksFlow(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>, errorCode: Int) {
        tracks.clear()
        if (errorCode == 200 && foundTracks.isNotEmpty()) {
            tracks.addAll(foundTracks)
            screenStateLiveData.postValue(ListUiState.Content(tracks))
        } else {
            showMessage(errorCode)
        }
    }

    private fun showMessage(responseCode: Int) {
        screenStateLiveData.postValue(if (responseCode == 0) (ListUiState.Empty as ListUiState<Track>) else TrackListUiState.LinkError as ListUiState<Track>)
    }

    fun showTrack(track: Track) {
        showAction(track)
    }

    fun showFoundTracks(changedText: String) {
        screenStateLiveData.postValue(ListUiState.Loading as ListUiState<Track>)
        searchDebounce(changedText)
    }

    fun showHistory() {
        screenStateLiveData.postValue(ListUiState.Loading as ListUiState<Track>)
        loadHistory()
    }

    fun clearHistory() {
        historyTracks.clear()
        screenStateLiveData.postValue(TrackListUiState.EmptyHistory as ListUiState<Track>)
    }

    private fun searchDebounce(changedText: String) {

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    override fun onClickDebounce(item: Track) {
        updateHistoryJob?.cancel()
        updateHistoryJob = viewModelScope.launch {
            updateHistoryQueueUseCase.executeFlow(
                item,
                historyTracks,
            )
                .collect { tracksState ->

                    screenStateLiveData.postValue(tracksState)
                }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
