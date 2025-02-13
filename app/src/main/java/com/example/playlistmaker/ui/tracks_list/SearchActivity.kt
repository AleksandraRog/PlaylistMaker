package com.example.playlistmaker.ui.tracks_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.model.HistoryQueue
import com.example.playlistmaker.R
import com.example.playlistmaker.ScreenSize
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.interactors.HistoryInteractor
import com.example.playlistmaker.domain.interactors.TracksInteractor
import com.example.playlistmaker.presentation.mapper.SizeFormatter
import com.example.playlistmaker.ui.CustomCircularProgressIndicator
import com.example.playlistmaker.ui.MainActivity
import com.example.playlistmaker.ui.PlayerActivity
import java.lang.ref.WeakReference
import java.util.LinkedList

class SearchActivity : AppCompatActivity() {

    private var searchTextValue: String = EDIT_TEXT_DEF
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val historyInteractor = Creator.provideHistoryInteractor()
    private val updateHistoryQueueUseCase = Creator.provideUpdateHistoryQueueUseCase()
    private val tracks = ArrayList<Track>()
    private var historyTracks = HistoryQueue(LinkedList<Track>())
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val historyLinearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    private val searchRunnable = Runnable { search() }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true


    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var includeView: ViewGroup
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var errorView: View
    private lateinit var historyView: View
    private lateinit var clearButton: ImageView
    private lateinit var topToolbar: Toolbar
    private lateinit var inflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ScreenSize.initSizeTrackViewHolder(this)

        setOnApplyWindowInsetsListener(findViewById<View>(R.id.search_activity)) { view, insets ->
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

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        clearButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            inputEditText.setText("")
        }

        placeholderButton.setOnClickListener {
            showProgressBar()
            search()
        }

        clearHistoryButton.setOnClickListener {

            historyTracks.clear()
            historyTrackAdapter.updateTracks(LinkedList(historyTracks).asReversed())
            includeView.removeAllViews()
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
                searchTextValue = s.toString()
                if (searchTextValue == "" && inputEditText.hasFocus()) {// && historyTracks.size != 0) {
                    showHistory()
                } else {
                    if (includeView.childCount > 0) {
                        clearAll()
                    }
                    showFoundTracks()

                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) { //&& historyTracks.size != 0) {
                showHistory()
            } else if (includeView.childCount > 0) {
                clearAll()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mainIntent = Intent(this@SearchActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        })
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

        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        topToolbar = findViewById(R.id.top_toolbar_frame)
        includeView = findViewById(R.id.includeView)
        inflater = LayoutInflater.from(this)
        errorView = inflater.inflate(R.layout.error_view_group, null)
        placeholderMessage = errorView.findViewById(R.id.placeholderMessage)
        placeholderButton = errorView.findViewById(R.id.renovate)
        historyView = inflater.inflate(R.layout.history_view_group, null)
        clearHistoryButton = historyView.findViewById(R.id.clear_history_button)
    }

    private fun showHistory() {
        showProgressBar()
        loadHistory()
    }

    private fun showProgressBar() {
        val progressBar = CustomCircularProgressIndicator(this).getCustomCircularProgressIndicator()
        updateIncludeView(progressBar)
    }

    private fun loadHistory() {

        historyInteractor.loadTracks(consumer = object : HistoryInteractor.HistoryTracksConsumer {
            override fun consume(data: ConsumerData<LinkedList<Track>>) {

                val history = data.result
                val historyRunnable = Runnable {
                    historyTracks = HistoryQueue(history)
                    if (historyTracks.size != 0) {
                        showHistoryView()
                    } else {
                        includeView.removeAllViews()
                    }
                }
                handler.post(historyRunnable)
            }

        })
    }

    private fun showHistoryView() {
        initHistoryView()
        historyTrackAdapter.tracks = LinkedList(historyTracks).asReversed()
        historyTrackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                showTrack(track)
                historyTrackAdapter.updateTracks(
                    LinkedList(historyTracks).asReversed()
                )
                historyLinearLayoutManager.scrollToPosition(0)
            }
        }
        updateIncludeView(historyView)
        historyTrackAdapter.updateTracks(LinkedList(historyTracks).asReversed())
        historyLinearLayoutManager.scrollToPosition(0)
    }

    private fun initHistoryView(){
        historyRecyclerView = historyView.findViewById(R.id.trackList2)
        historyRecyclerView.adapter = historyTrackAdapter
        historyRecyclerView.layoutManager = historyLinearLayoutManager
    }

    private fun showFoundTracks() {
        showProgressBar()
        initListFoundTracksView()
        trackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                showTrack(track)
            }
        }
        searchDebounce()
    }

    private fun initListFoundTracksView(){

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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun search(): Boolean {

        tracksInteractor.searchTracks(
            inputEditText.text.toString(),
            consumer = object : TracksInteractor.FindTracksConsumer {
                override fun consume(data: ConsumerData<List<Track>>) {
                    val searchRunnable = Runnable {
                        tracks.clear()
                        if (data.code == 200) {
                            tracks.addAll(data.result)
                            updateIncludeView(recyclerView)
                            trackAdapter.updateTracks(tracks)

                        } else {
                            showMessage(data.code)

                        }
                    }
                    handler.post(searchRunnable)
                }
            })
        return true
    }

    private fun showMessage(responseCode: Int) {

        when (responseCode) {
            0 -> {
                placeholderMessage.visibility = View.VISIBLE
                placeholderButton.visibility = View.GONE
                placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getDrawable(R.drawable.ic_vector_nomusic),
                    null,
                    null
                )
                placeholderMessage.text = getString(R.string.nothing_found)
            }

            else -> {
                placeholderMessage.visibility = View.VISIBLE
                placeholderButton.visibility = View.VISIBLE
                placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getDrawable(R.drawable.ic_vector_nolink),
                    null,
                    null
                )
                placeholderMessage.text = getString(R.string.something_went_wrong)

            }
        }
        updateIncludeView(errorView)
    }

    private fun showTrack(track: Track) {

        val weakContext = WeakReference(this).get()
        updateHistoryQueueUseCase.execute(
            track,
            historyTracks,
            consumer = object : Consumer<Track> {
                override fun consume(data: ConsumerData<Track>) {
                    val historyRunnable = Runnable {
                        val playerIntent = Intent(weakContext, PlayerActivity::class.java)
                        playerIntent.putExtra(TRACK_EXTRA, data.result.trackId)
                        startActivity(playerIntent)
                    }
                    handler.post(historyRunnable)
                }
            }
        )
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun clearAll() {
        includeView.removeAllViews()
        tracks.clear()
        trackAdapter.updateTracks(tracks)
    }

    private fun updateIncludeView(includedView: View) {
        includeView.removeAllViews()
        includeView.addView(includedView)
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

    companion object {
        const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        const val EDIT_TEXT_DEF = ""
        const val TRACK_EXTRA = "TRACK_EXTRA"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
