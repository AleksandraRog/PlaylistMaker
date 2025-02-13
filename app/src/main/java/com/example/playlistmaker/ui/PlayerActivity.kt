package com.example.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.model.TrackTimePeriod
import com.example.playlistmaker.domain.usecase.UpdateTimerTaskUseCase
import com.example.playlistmaker.presentation.mapper.SizeFormatter
import com.example.playlistmaker.ui.tracks_list.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var releaseDate: TextView
    private lateinit var collectionName: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var currentTrackTime: TextView
    private lateinit var playButton: ImageView
    private lateinit var track: Track
    private lateinit var topToolbar: Toolbar
    private lateinit var likeButton: ToggleButton
    private lateinit var addButton: ImageButton

    private var playerState = PlayerState.STATE_DEFAULT
    private var mainThreadHandler: Handler? = null
    private var currentTrackTimeInMillis = 0L
    private val getTrackByIdUseCase = Creator.provideGetTrackByIdUseCase()
    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()

    companion object {
        private const val DELAY = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        setOnApplyWindowInsetsListener(findViewById<View>(R.id.player_activity)) { view, insets ->
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

        initPlayerView()

        mainThreadHandler = Handler(Looper.getMainLooper())
        val trackId = intent.getIntExtra(SearchActivity.TRACK_EXTRA, -1)
        findTrackById(trackId)

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        likeButton.setOnCheckedChangeListener { _, isChecked ->

        }

        addButton.setOnClickListener {

        }
    }

    private fun findTrackById(trackId: Int) {

        getTrackByIdUseCase.execute(trackId, consumer = object : Consumer<Track> {
            override fun consume(data: ConsumerData<Track>) {
                val historyRunnable = Runnable {
                    track = data.result
                    showTrackView(track)
                    playButton.setOnClickListener {
                        playbackControl()
                    }
                    preparePlayer(track.previewUrl)
                }
                mainThreadHandler?.post(historyRunnable)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun initPlayerView() {

        imageView = findViewById(R.id.trackImage)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        releaseDate = findViewById(R.id.releaseDate)
        collectionName = findViewById(R.id.collectionName)
        primaryGenreName = findViewById(R.id.primaryGenreName)
        country = findViewById(R.id.country)
        currentTrackTime = findViewById(R.id.currentTrackTime)
        playButton = findViewById(R.id.play_button)
        topToolbar = findViewById(R.id.top_toolbar_frame)
        likeButton = findViewById(R.id.like_button)
        addButton = findViewById(R.id.add_button)
    }

    private fun showTrackView(track: Track) {

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(SizeFormatter.dpToPx(8f, this)))
            .into(imageView)
        val placeholderString = this.getString(R.string.no_reply)
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime?.toString() ?: placeholderString

        releaseDate.text = if (track.releaseDate == null) placeholderString else {
            SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
        }
        collectionName.text = track.collectionName
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        currentTrackTime.text = TrackTimePeriod.fromMillis(currentTrackTimeInMillis).toString()
    }

    private fun preparePlayer(url: String) {

        audioPlayerInteractor.prepare(
            url,
            prepareConsumer = object : AudioPlayerInteractor.PlayerStateConsumer {
                override fun consume(data: ConsumerData<PlayerState>) {
                    val prepareRunnable = Runnable {
                        playButton.isEnabled = true
                        playerState = data.result
                    }
                    mainThreadHandler?.post(prepareRunnable)
                }

            },
            completionConsumer = object : AudioPlayerInteractor.PlayerStateConsumer {
                override fun consume(data: ConsumerData<PlayerState>) {
                    val finishRunnable = Runnable {
                        playButton.isSelected = false
                        playerState = data.result
                        currentTrackTimeInMillis = 0L
                        currentTrackTime.text = TrackTimePeriod.fromMillis(currentTrackTimeInMillis).toString()
                    }
                    mainThreadHandler?.post(finishRunnable)
                }
            })
    }

    private fun startPlayer() {

        audioPlayerInteractor.play(consumer = object : AudioPlayerInteractor.PlayerStateConsumer {
            override fun consume(data: ConsumerData<PlayerState>) {
                val startPlayerRunnable = Runnable {
                    playButton.isSelected = true
                    playerState = data.result
                    startTimer()
                }
                mainThreadHandler?.post(startPlayerRunnable)
            }
        })
    }

    private fun pausePlayer() {

        audioPlayerInteractor.stop(consumer = object : AudioPlayerInteractor.PlayerStateConsumer {
            override fun consume(data: ConsumerData<PlayerState>) {
                val stopPlayerRunnable = Runnable {
                    playButton.isSelected = false
                    playerState = data.result
                    mainThreadHandler?.removeCallbacksAndMessages(null)
                }
                mainThreadHandler?.post(stopPlayerRunnable)
            }
        })
    }

    private fun playbackControl() {

        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> {}
        }
    }

    private fun startTimer() {
        val startTime = System.currentTimeMillis()
        val currentTime = currentTrackTimeInMillis
        val timerRunnable = object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    currentTrackTimeInMillis = UpdateTimerTaskUseCase.execute(
                        startTime,
                        currentTime
                    )
                    currentTrackTime.text =
                        TrackTimePeriod.fromMillis(currentTrackTimeInMillis).toString()
                    mainThreadHandler?.postDelayed(this, DELAY)
                }
            }
        }
        mainThreadHandler?.post(timerRunnable)
    }
}
