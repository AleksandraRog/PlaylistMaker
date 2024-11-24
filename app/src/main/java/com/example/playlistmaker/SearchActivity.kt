package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.LinkedList

class SearchActivity : AppCompatActivity() {

    private var searchTextValue: String = EDIT_TEXT_DEF

    private val iTunsService: TrackApi by inject()
    private val tracks = ArrayList<Track>()
    private var historyTracks = HistoryQueue(LinkedList<Track>())
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val searchRunnable = Runnable { search() }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
        .create()

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var includeView: ViewGroup
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorView: View
    private lateinit var historyView: View
    private lateinit var progressBar: ProgressBar

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

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)


        includeView = findViewById(R.id.includeView)

        val inflater = LayoutInflater.from(this)

        errorView = inflater.inflate(R.layout.error_view_group, null)
        placeholderMessage = errorView.findViewById(R.id.placeholderMessage)
        placeholderButton = errorView.findViewById(R.id.renovate)

        historyView = inflater.inflate(R.layout.history_view_group, null)
        clearHistoryButton = historyView.findViewById(R.id.clear_history_button)

        recyclerView = RecyclerView(this).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(16f)
            }
        }

        progressBar = CreateCircularProgressIndicator()

        trackAdapter.tracks = tracks

        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                addHistoryList(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                val trackJson = trackSerializable(track)
                playerIntent.putExtra(TRACK_EXTRA, trackJson)
                startActivity(playerIntent)
            }
        }

        val historyRecyclerView = historyView.findViewById<RecyclerView>(R.id.trackList2)
        historyTracks = HistoryQueue(loadHistoryList(this))
        historyTrackAdapter.tracks = LinkedList(historyTracks).asReversed()
        historyRecyclerView.adapter = historyTrackAdapter

        val historyLinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        historyRecyclerView.layoutManager = historyLinearLayoutManager
        historyTrackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                addHistoryList(track)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                val trackJson = trackSerializable(track)
                playerIntent.putExtra(TRACK_EXTRA, trackJson)
                startActivity(playerIntent)
                historyTrackAdapter.updateTracks(
                    LinkedList(historyTracks).asReversed()
                )
                historyLinearLayoutManager.scrollToPosition(0)
            }
        }

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
                if (searchTextValue == "" && inputEditText.hasFocus() && historyTracks.size != 0) {

                    updateIncludeView(historyView)
                    historyTrackAdapter.updateTracks(LinkedList(historyTracks).asReversed())
                    historyLinearLayoutManager.scrollToPosition(0)
                } else {
                    if (includeView.childCount > 0) {clearAll()}
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyTracks.size != 0) {
                updateIncludeView(historyView)
                historyTrackAdapter.updateTracks(LinkedList(historyTracks).asReversed())
            } else if (includeView.childCount > 0) {
                clearAll()
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EDIT_TEXT_KEY", searchTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getString(EDIT_TEXT_KEY, EDIT_TEXT_DEF)
    }

    private fun search(): Boolean {

        includeView.addView(progressBar)
        progressBar.visibility = View.VISIBLE

        if (inputEditText.text.isNotEmpty()) {
            iTunsService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TracksResponse> {

                    var includedView: View = errorView

                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        val responseCode = response.code()
                        val responseBody = response.body()?.results
                        tracks.clear()
                        if (responseBody?.isNotEmpty() == true && responseCode == 200) {
                            tracks.addAll(responseBody)
                            includedView = recyclerView
                        }
                        updateIncludeView(includedView)
                        trackAdapter.updateTracks(tracks)
                        showMessage(responseCode, responseBody?.isNotEmpty())
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        updateIncludeView(includedView)
                        showMessage(500, false)
                    }

                })
        }
        return true
    }

    private fun showMessage(responseCode: Int, responseRezult: Boolean?) {

        if (responseCode != 200) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderButton.visibility = View.VISIBLE
            placeholderMessage.setCompoundDrawablesWithIntrinsicBounds(
                null,
                getDrawable(R.drawable.ic_vector_nolink),
                null,
                null
            )
            placeholderMessage.text = getString(R.string.something_went_wrong)

        } else if (responseRezult != true) {

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

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            this.resources.displayMetrics
        ).toInt()
    }

    private fun loadHistoryList(context: Context): LinkedList<Track> {
        val json = context.getSharedPreferences(
            PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE
        ).getString(HISTORY_LIST_KEY, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<LinkedList<Track>>() {}.type)
        } else LinkedList<Track>()
    }

    private fun addHistoryList(track: Track) {
        historyTracks.removeIf { it.trackId == track.trackId }
        if (historyTracks.size == MAX_HISTORYLIST_SIZE) {
            historyTracks.poll()
        }
        historyTracks.offer(track)
    }

    private fun trackSerializable(track: Track) : String {
        return gson.toJson(track)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
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

    private fun CreateCircularProgressIndicator() : CircularProgressIndicator {
        return CircularProgressIndicator(this).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = CENTER_HORIZONTAL
                topMargin = dpToPx(140f)
            }

            isIndeterminate  = true
            indicatorSize = dpToPx(44f)
            progress = 75
            setIndicatorColor(getColor(R.color.YP_blue))
        }
    }

    companion object {
        const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        const val EDIT_TEXT_DEF = ""
        const val MAX_HISTORYLIST_SIZE = 10
        const val TRACK_EXTRA = "TRACK_EXTRA"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
