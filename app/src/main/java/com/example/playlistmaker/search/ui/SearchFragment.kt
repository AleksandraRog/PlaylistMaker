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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.common.presentation.model.TopicalFragment
import com.example.playlistmaker.common.ui.fragments.IncludeFragment
import com.example.playlistmaker.common.ui.recycler_components.common.RecyclerAdapter
import com.example.playlistmaker.common.ui.recycler_components.track.TrackAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.databinding.ErrorViewGroupBinding
import com.example.playlistmaker.databinding.HistoryViewGroupBinding
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.presentation.TrackListUiState
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.LinkedList

class SearchFragment() : IncludeFragment<ActivitySearchBinding, Track, >() {


    private lateinit var inputEditText: EditText
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var simpleTextWatcher: TextWatcher
    private lateinit var errorBinding: ErrorViewGroupBinding
    private lateinit var historyBinding: HistoryViewGroupBinding
    private lateinit var historyLinearLayoutManager: LinearLayoutManager

    private val viewModel: SearchViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()
    private var searchTextValue: String = EDIT_TEXT_DEF
    private val historyTrackAdapter = TrackAdapter()
    override val adapter: RecyclerAdapter<Track> = TrackAdapter()
    override val navigateIdAction: Int = R.id.action_searchFragment_to_playerActivity
    override val extraActionBundleKey: ExtraActionBundleKey = ExtraActionBundleKey.TRACK_EXTRA_HISTORY

    override fun createBinding(
        increateBindingflater: LayoutInflater,
        container: ViewGroup?
    ): ActivitySearchBinding {
        errorBinding = ErrorViewGroupBinding.inflate(layoutInflater)
        historyBinding = HistoryViewGroupBinding.inflate(layoutInflater)
        mainViewModel.setFragmentScreen(TopicalFragment.SEARCH)
        return ActivitySearchBinding.inflate(increateBindingflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListTrackView()

        if (savedInstanceState!=null){
            searchTextValue = savedInstanceState.getString(EDIT_TEXT_KEY, EDIT_TEXT_DEF)
            inputEditText.setText(searchTextValue)
            if(savedInstanceState.getBoolean("EDIT_FOCUS_KEY", false)) {inputEditText.requestFocus()}
        }

        viewModel.observeState.observe(viewLifecycleOwner) {

            if(it is (TrackListUiState.History)){
                inputEditText.requestFocus()
            }
            showContentRender(it)
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
                        if (includeView.isNotEmpty()) {
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

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.showHistory()
            } else if (this.includeView.isNotEmpty()) {
                updateIncludeViewByClear()
            }
        }

        adapter.setOnItemClickListener = { track ->
            viewModel.showTrack(track)
        }

        historyTrackAdapter.setOnItemClickListener = { track ->
            viewModel.showTrack(track)
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
    }

    override fun goToIntent(entityId: Int) {
        super.goToIntent(entityId)
        viewModel.restoreState()
    }

    override fun renderUiIncludeState(state: ListUiState.ListUiIncludeState<Track>) {
        when (state) {
            is TrackListUiState.History -> updateIncludeViewByHistory(state.tracks)
            is TrackListUiState.EmptyHistory  -> updateIncludeViewByClear()
            is TrackListUiState.LinkError -> showLinkError()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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
        historyTrackAdapter.list = LinkedList(historyTracks).asReversed()
        updateIncludeView(historyBinding.root)
        historyTrackAdapter.updateList(LinkedList(historyTracks).asReversed())
        historyLinearLayoutManager.scrollToPosition(0)
    }

    private fun updateIncludeViewByError() {
        updateIncludeView(errorBinding.root)
    }

    override fun updateIncludeViewByEmpty() {
        showNothingError()
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

    companion object {
        const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        const val EDIT_TEXT_DEF = ""
    }
}
