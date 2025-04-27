package com.example.playlistmaker.common.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class ActionViewModel<T> : ViewModel() {

    var screenStateLiveData = MutableLiveData<ListUiState<T>>()
    val previousScreenStateLiveData = MutableLiveData<ListUiState<T>>()
    private var clickJob: Job? = null
    private var current = true

    val observeState = MediatorLiveData<ListUiState<T>>().apply {
        addSource(screenStateLiveData) { newValue ->
            previousScreenStateLiveData.value = this.value
            this.value = newValue
        }
    }

    fun restoreState() {
        screenStateLiveData.postValue(previousScreenStateLiveData.value)
    }

    open fun showAction (item : T) {
        clickDebounce(item, ::onClickDebounce)
    }

    protected abstract fun onClickDebounce(item: T)

    private fun clickDebounce (item: T, action: (T) -> Unit) {
        if (!current) return
        if (current) {
            current = false
            clickJob = viewModelScope.launch {
                action(item)
                delay(CLICK_DEBOUNCE_DELAY)
                current = true
            }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
