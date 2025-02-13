package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
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
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.presentation.TracksState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.LinkedList

class SearchActivity : AppCompatActivity() {

    private lateinit var includeView: ViewGroup
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var simpleTextWatcher: TextWatcher
    private lateinit var binding: ActivitySearchBinding
    private lateinit var errorBinding: ErrorViewGroupBinding
    private lateinit var historyBinding: HistoryViewGroupBinding

    private val viewModel: SearchViewModel by viewModel()
    private val progressBar: ProgressBar by lazy { CustomCircularProgressIndicator(this) }
    private var searchTextValue: String = EDIT_TEXT_DEF
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val historyLinearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

   override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        errorBinding = ErrorViewGroupBinding.inflate(layoutInflater)
        historyBinding = HistoryViewGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ScreenSize.initSizeTrackViewHolder(this)

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val paddingBottom = if (ime.bottom > 0) ime.bottom else
                systemBars.bottom
            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                paddingBottom
            )
            insets
        }

       initListTrackView()

       viewModel.observeState().observe(this) {
            render(it)
        }

        binding.topToolbarFrame.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.clearIcon.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
                if (searchTextValue == "" && inputEditText.hasFocus()) {// && historyTracks.size != 0) {
                    viewModel.showHistory()
                    historyLinearLayoutManager.scrollToPosition(0)
                } else {
                    if (includeView.childCount > 0) {
                        updateIncludeViewByClear()
                    }
                    viewModel.showFoundTracks(searchTextValue)
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
                historyLinearLayoutManager.scrollToPosition(0)
            } else if (includeView.childCount > 0) {
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

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mainIntent = Intent(this@SearchActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher.let { inputEditText.removeTextChangedListener(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EDIT_TEXT_KEY", searchTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getString(EDIT_TEXT_KEY, EDIT_TEXT_DEF)
    }

    private fun initListTrackView() {
        inputEditText = binding.inputEditText
        includeView = findViewById(R.id.includeView)
    }

    private fun goToPlayerActivity(trackId: Int) {

        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra(TRACK_EXTRA, trackId)
        startActivity(playerIntent)
    }

    private fun initListFoundTracksView() {

        recyclerView = RecyclerView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = SizeFormatter.dpToPx(16f, context)
            }
        }
        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            val clickDebounceRunnable = Runnable {
                isClickAllowed = true
            }
            handler.removeCallbacks(clickDebounceRunnable)
            handler.postDelayed(clickDebounceRunnable, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun initHistoryView() {
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
            AppCompatResources.getDrawable(this, R.drawable.ic_vector_nolink),
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
            AppCompatResources.getDrawable(this, R.drawable.ic_vector_nomusic),
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
            is TracksState.EmptyHistory -> updateIncludeViewByClear()
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
