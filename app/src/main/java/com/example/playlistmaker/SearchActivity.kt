package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchTextValue: String = EDIT_TEXT_DEF
    private val itunsBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunsBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunsService = retrofit.create(TrackApi::class.java)
    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        ScreenSize.initSizeTrackViewHolder(this)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)
        val recyclerView = findViewById<RecyclerView>(R.id.trackList)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.renovate)
        trackAdapter.tracks = tracks

        recyclerView.adapter = trackAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        setSupportActionBar(topToolbar)

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        clearButton.setOnClickListener {

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            inputEditText.setText("")
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        placeholderButton.setOnClickListener {
            search(inputEditText)
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchTextValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(inputEditText)
            }
            false
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EDIT_TEXT_KEY", searchTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getString(EDIT_TEXT_KEY, EDIT_TEXT_DEF)
    }

    private fun search(view: EditText): Boolean {

        if (view.text.isNotEmpty()) {
            itunsService.search(view.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        val responseCode = response.code()
                        val responseBody = response.body()?.results
                        when (responseCode) {
                            200 -> {
                                tracks.clear()
                                if (responseBody?.isNotEmpty() == true) {
                                    tracks.addAll(responseBody!!.map { track ->
                                        track.trackTime()
                                        track
                                    })
                                }
                                trackAdapter.notifyDataSetChanged()
                                showMessage(responseCode, responseBody?.isNotEmpty())
                            }
                            else -> showMessage(responseCode, responseBody?.isNotEmpty())
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
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

        } else {
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
        }
    }

    companion object {
        const val EDIT_TEXT_KEY = "EDIT_TEXT_KEY"
        const val EDIT_TEXT_DEF = ""
    }
}
