package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import com.example.playlistmaker.common.ui.CustomCircularProgressIndicator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.databinding.ErrorViewGroupBinding
import com.example.playlistmaker.databinding.HistoryViewGroupBinding
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.presentation.TracksState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.LinkedList

class SearchFragment : Fragment() {

    private lateinit var includeView: ViewGroup
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var simpleTextWatcher: TextWatcher
    private lateinit var binding: ActivitySearchBinding
    private lateinit var errorBinding: ErrorViewGroupBinding
    private lateinit var historyBinding: HistoryViewGroupBinding
    private lateinit var historyLinearLayoutManager: LinearLayoutManager

    private val viewModel: SearchViewModel by viewModel()
    private val progressBar: ProgressBar by lazy { CustomCircularProgressIndicator(requireContext()) }
    private var searchTextValue: String = EDIT_TEXT_DEF
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenSize.initSizeTrackViewHolder(requireContext())
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        errorBinding = ErrorViewGroupBinding.inflate(layoutInflater)
        historyBinding = HistoryViewGroupBinding.inflate(layoutInflater)
        binding = ActivitySearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListTrackView()

        if (savedInstanceState!=null){
            searchTextValue = savedInstanceState.getString(EDIT_TEXT_KEY, EDIT_TEXT_DEF)

            if(savedInstanceState.getBoolean("EDIT_FOCUS_KEY", false)) {inputEditText.requestFocus()}
        }

        viewModel.observeState.observe(viewLifecycleOwner) {

            if(it is TracksState.History){
                inputEditText.requestFocus()
            }
            render(it)
        }

        binding.clearIcon.setOnClickListener {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearIcon.windowToken, 0)
            inputEditText.setText("")
        }

        errorBinding.renovate.setOnClickListener {
            viewModel.showFoundTracks(searchTextValue)
        }

        historyBinding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clearIcon.visibility = clearButtonVisibility(s)
                searchTextValue = s.toString()
                if (inputEditText.hasFocus()) {
                    if (searchTextValue == "") {
                        viewModel.showHistory()

                    } else {
                        if (includeView.childCount > 0) {
                            updateIncludeViewByClear()
                        }

                        viewModel.showFoundTracks(searchTextValue)

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.showHistory()
            } else if (this.includeView.isNotEmpty()) {
                updateIncludeViewByClear()
            }
        }

        trackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                viewModel.showTrack(track)
            }
        }

        historyTrackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                viewModel.showTrack(track)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleTextWatcher.let { inputEditText.removeTextChangedListener(it) }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EDIT_TEXT_KEY", searchTextValue)
        outState.putBoolean("EDIT_FOCUS_KEY", inputEditText.hasFocus())
    }

    private fun initListTrackView() {
        inputEditText = binding.inputEditText
        includeView = requireActivity().findViewById(R.id.includeView)
    }

    private fun goToPlayerActivity(trackId: Int) {

        val bundleTrackId = Bundle().apply {
           putInt(TRACK_EXTRA, trackId)
        }
        findNavController().navigate(R.id.action_searchFragment_to_playerActivity, bundleTrackId)
        viewModel.restoreState()
    }

    private fun initListFoundTracksView() {

        recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = SizeFormatter.dpToPx(16f, context)
            }
        }
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun clickDebounce(): Boolean {

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

    private fun initHistoryView() {
        historyLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        historyRecyclerView = historyBinding.historyRecyclerview
        historyRecyclerView.adapter = historyTrackAdapter
        historyRecyclerView.layoutManager = historyLinearLayoutManager
    }

    private fun updateIncludeViewByHistory(historyTracks: HistoryQueue) {
        initHistoryView()
        historyTrackAdapter.tracks = LinkedList(historyTracks).asReversed()
        updateIncludeView(historyBinding.root)
        historyTrackAdapter.updateTracks(LinkedList(historyTracks).asReversed())
        historyLinearLayoutManager.scrollToPosition(0)
    }

    private fun updateIncludeViewByProgressBar() {
        updateIncludeView(progressBar)
    }

    private fun updateIncludeViewByError() {
        updateIncludeView(errorBinding.root)
    }

    private fun updateIncludeViewByFoundTracks(tracks: List<Track>) {
        initListFoundTracksView()
        updateIncludeView(recyclerView)
        trackAdapter.updateTracks(tracks)
    }

    fun updateIncludeViewByClear() {
        includeView.removeAllViews()
    }

    private fun updateIncludeView(includedView: View?) {
        includeView.removeAllViews()
        includeView.addView(includedView)
    }

    private fun showLinkError() {
        errorBinding.placeholderMessage.visibility = View.VISIBLE
        errorBinding.renovate.visibility = View.VISIBLE
        errorBinding.placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
            null,
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_vector_nolink),
            null,
            null
        )
        errorBinding.placeholderMessage.text = getString(R.string.something_went_wrong)
        updateIncludeViewByError()
    }

    private fun showNothingError() {
        errorBinding.placeholderMessage.visibility = View.VISIBLE
        errorBinding.renovate.visibility = View.GONE
        errorBinding.placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
            null,
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_vector_nomusic),
            null,
            null
        )
        errorBinding.placeholderMessage.text = getString(R.string.nothing_found)
        updateIncludeViewByError()
    }

    private fun render(state: TracksState) {

        when (state) {
            is TracksState.Loading -> updateIncludeViewByProgressBar()
            is TracksState.Content -> updateIncludeViewByFoundTracks(state.tracks)
            is TracksState.History -> updateIncludeViewByHistory(state.tracks)
            is TracksState.EmptyHistory, TracksState.Default -> updateIncludeViewByClear()
            is TracksState.Empty -> showNothingError()
            is TracksState.LinkError -> showLinkError()
            is TracksState.AnyTrack -> goToPlayerActivity(state.trackId)
        }
    }

    companion object {
        const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        const val EDIT_TEXT_DEF = ""
        const val TRACK_EXTRA = "TRACK_EXTRA"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
