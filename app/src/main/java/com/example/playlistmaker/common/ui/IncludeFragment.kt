package com.example.playlistmaker.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.common.domain.model.ExtraActionBundleKey
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.UiState
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class IncludeFragment<T : ViewBinding> : Fragment() {

    lateinit var includeView: ViewGroup
    private lateinit var recyclerView: RecyclerView
    private val progressBar: ProgressBar by lazy { CustomCircularProgressIndicator(requireContext()) }
    val trackAdapter = TrackAdapter()
    private var isClickAllowed = true
    lateinit var binding: T

    abstract val extraActionBundleKey: ExtraActionBundleKey
    abstract val navigateIdAction: Int

    abstract fun createBinding(increateBindingflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenSize.initSizeTrackViewHolder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = createBinding(inflater, container)
        includeView = binding.root.findViewById(R.id.includeView)
        return binding.root
    }

    fun showContentRender(state: UiState) {
        when (state) {
            is UiState.AnyTrack -> goToPlayerActivity(state.trackId)
            is UiState.Content -> updateIncludeViewByTrackList(state.tracks)
            UiState.Default -> updateIncludeViewByClear()
            UiState.Empty -> updateIncludeViewByEmpty()
            UiState.Loading -> updateIncludeViewByProgressBar()
            is UiState.UiIncludeState -> renderUiIncludeState(state)
        }
    }

    open fun goToPlayerActivity(trackId: Int) {

        val bundleTrackId = Bundle().apply {
            putInt(extraActionBundleKey.value, trackId)
        }
        findNavController().navigate(navigateIdAction, bundleTrackId)
    }

    private fun initListTracksView() {

        recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = SizeFormatter.dpToPx(16f, context)
            }
        }
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    fun clickDebounce(): Boolean {

        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    open fun updateIncludeViewByProgressBar() {
        updateIncludeView(progressBar)
    }

    open fun updateIncludeViewByTrackList(tracks: List<Track>) {
        initListTracksView()
        updateIncludeView(recyclerView)
        trackAdapter.updateTracks(tracks)
    }

    open fun updateIncludeViewByClear() {
        includeView.removeAllViews()
    }

    open fun updateIncludeViewByEmpty() {
        updateIncludeViewByClear()
    }

    fun updateIncludeView(includedView: View?) {
        includeView.removeAllViews()
        includeView.addView(includedView)
    }

    abstract fun renderUiIncludeState(state: UiState.UiIncludeState)

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}