package com.example.playlistmaker.search.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.TrackListUiState
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.domain.usecase.UpdateHistoryQueueUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.LinkedList

class SearchViewModel(application: Application,
    private val historyInteractor: HistoryInteractor,
    private val tracksInteractor: TracksInteractor,
    private val updateHistoryQueueUseCase: UpdateHistoryQueueUseCase
) : AndroidViewModel(application) {
    private val stateLiveData = MutableLiveData<ListUiState<Track>>()
    private val previousStateLiveData = MutableLiveData<ListUiState<Track>>()
    private var searchJob: Job? = null

    init {
        stateLiveData.value = ListUiState.Default as ListUiState<Track>
    }

    private val tracks = ArrayList<Track>()
    private var historyTracks = HistoryQueue(LinkedList())

    val observeState = MediatorLiveData<ListUiState<Track>>().apply {
        addSource(stateLiveData) { newValue ->
            previousStateLiveData.value = this.value
            this.value = newValue
        }
    }

    fun restoreState() {
        stateLiveData.postValue(previousStateLiveData.value)
    }

    private fun loadHistory() {
        viewModelScope.launch {
            historyInteractor.loadTracksFlow()
                .collect { pair ->
                    historyTracks = HistoryQueue(pair.first)
                                    val trackListUiState =
                    if (historyTracks.size != 0) TrackListUiState.History(historyTracks) else TrackListUiState.EmptyHistory
                stateLiveData.postValue(trackListUiState)
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
            stateLiveData.postValue(ListUiState.Content(tracks))
        } else {
            showMessage(errorCode)
        }
    }

    private fun showMessage(responseCode: Int) {
        stateLiveData.postValue(if (responseCode == 0) (ListUiState.Empty as ListUiState<Track>) else TrackListUiState.LinkError as ListUiState<Track>)
    }

    fun showTrack(track: Track) {
        viewModelScope.launch {

            updateHistoryQueueUseCase.executeFlow(
                track,
                historyTracks,)
                .collect { tracksState ->

                    stateLiveData.postValue(tracksState)
                }
        }
    }

    fun showFoundTracks(changedText: String) {
        stateLiveData.postValue(ListUiState.Loading as ListUiState<Track>)
        searchDebounce(changedText)
    }

    fun showHistory() {
        stateLiveData.postValue(ListUiState.Loading as ListUiState<Track>)
        loadHistory()
    }

    fun clearHistory() {
        historyTracks.clear()
        stateLiveData.postValue(TrackListUiState.EmptyHistory as ListUiState<Track>)
    }

    private fun searchDebounce(changedText: String) {

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
