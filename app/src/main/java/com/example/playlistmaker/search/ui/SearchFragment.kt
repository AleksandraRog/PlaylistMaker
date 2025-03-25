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
import com.example.playlistmaker.common.domain.model.ExtraActionBundleKey
import com.example.playlistmaker.common.presentation.TracksState
import com.example.playlistmaker.common.presentation.UiState
import com.example.playlistmaker.common.ui.IncludeFragment
import com.example.playlistmaker.common.ui.TrackAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.databinding.ErrorViewGroupBinding
import com.example.playlistmaker.databinding.HistoryViewGroupBinding
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.LinkedList

class SearchFragment() : IncludeFragment<ActivitySearchBinding>() {


    private lateinit var inputEditText: EditText
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var simpleTextWatcher: TextWatcher
    private lateinit var errorBinding: ErrorViewGroupBinding
    private lateinit var historyBinding: HistoryViewGroupBinding
    private lateinit var historyLinearLayoutManager: LinearLayoutManager

    private val viewModel: SearchViewModel by viewModel()
    private var searchTextValue: String = EDIT_TEXT_DEF
    private val historyTrackAdapter = TrackAdapter()

    override val navigateIdAction: Int = R.id.action_searchFragment_to_playerActivity
    override val extraActionBundleKey: ExtraActionBundleKey = ExtraActionBundleKey.TRACK_EXTRA_HISTORY

    override fun createBinding(
        increateBindingflater: LayoutInflater,
        container: ViewGroup?
    ): ActivitySearchBinding {
        errorBinding = ErrorViewGroupBinding.inflate(layoutInflater)
        historyBinding = HistoryViewGroupBinding.inflate(layoutInflater)
        return ActivitySearchBinding.inflate(increateBindingflater, container, false)
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
    }

    override fun goToPlayerActivity(trackId: Int) {
        super.goToPlayerActivity(trackId)
        viewModel.restoreState()
    }

    override fun renderUiIncludeState(state: UiState.UiIncludeState) {
        when (state) {
            is TracksState.History -> updateIncludeViewByHistory(state.tracks)
            is TracksState.EmptyHistory  -> updateIncludeViewByClear()
            is TracksState.LinkError -> showLinkError()

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
        historyTrackAdapter.tracks = LinkedList(historyTracks).asReversed()
        updateIncludeView(historyBinding.root)
        historyTrackAdapter.updateTracks(LinkedList(historyTracks).asReversed())
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
