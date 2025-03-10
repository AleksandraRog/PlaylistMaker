package com.example.playlistmaker.search.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
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
    private val stateLiveData = MutableLiveData<TracksState>()
    private val previousStateLiveData = MutableLiveData<TracksState>()
    private var searchJob: Job? = null

    init {
        stateLiveData.value = TracksState.Default
    }

    val tracks = ArrayList<Track>()
    var historyTracks = HistoryQueue(LinkedList<Track>())

    val observeState = MediatorLiveData<TracksState>().apply {
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
                                    val tracksState =
                    if (historyTracks.size != 0) TracksState.History(historyTracks) else TracksState.EmptyHistory
                stateLiveData.postValue(tracksState)
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
            stateLiveData.postValue(TracksState.Content(tracks))
        } else {
            showMessage(errorCode)
        }
    }

    private fun showMessage(responseCode: Int) {
        stateLiveData.postValue(if (responseCode == 0) TracksState.Empty else TracksState.LinkError)
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
        stateLiveData.postValue(TracksState.Loading)
        searchDebounce(changedText)
    }

    fun showHistory() {
        stateLiveData.postValue(TracksState.Loading)
        loadHistory()
    }

    fun clearHistory() {
        historyTracks.clear()
        stateLiveData.postValue(TracksState.EmptyHistory)
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
