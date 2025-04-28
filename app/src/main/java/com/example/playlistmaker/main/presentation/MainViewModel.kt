package com.example.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.presentation.model.TopicalFragment

class MainViewModel :  ViewModel() {

    private var windowFocusLiveData = MutableLiveData<Boolean>()
    private var topicalFragmentLiveData = MutableLiveData<TopicalFragment>()

    fun setFragmentScreen(fragment: TopicalFragment){
        topicalFragmentLiveData.value = fragment
    }

    init{
        topicalFragmentLiveData.value = TopicalFragment.DEFAULT
    }

    fun getWindowFocusLiveData(): LiveData<Boolean> = windowFocusLiveData
    fun getTopicalFragmentLiveData(): LiveData<TopicalFragment> = topicalFragmentLiveData

    fun focusChange(screen: Boolean) {
        windowFocusLiveData.value = screen
    }
}
