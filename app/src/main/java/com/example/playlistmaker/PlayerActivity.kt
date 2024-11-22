package com.example.playlistmaker


import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Date
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
    private lateinit var play: ImageView
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null
    private var currentTrackTimeInMillis = 0L

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 1000L

    }

    private var playerState = STATE_DEFAULT

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
        val trackJson = intent.getStringExtra(SearchActivity.TRACK_EXTRA)
        val track = GsonBuilder()
            .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
            .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
            .create()
            .fromJson(trackJson, Track::class.java)


        imageView = this.findViewById(R.id.trackImage)
        trackName = this.findViewById(R.id.trackName)
        artistName = this.findViewById(R.id.artistName)
        trackTime = this.findViewById(R.id.trackTime)
        releaseDate = this.findViewById(R.id.releaseDate)
        collectionName = this.findViewById(R.id.collectionName)
        primaryGenreName = this.findViewById(R.id.primaryGenreName)
        country = this.findViewById(R.id.country)
        currentTrackTime = findViewById(R.id.currentTrackTime)


        Glide.with(this)
            .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")).centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f)))
            .into(imageView)
        val placeholderString = this.getString(R.string.no_reply)
        trackName.text = track.trackName ?: placeholderString
        artistName.text = track.artistName ?: placeholderString
        trackTime.text = track.trackTime?.toString() ?: placeholderString

        releaseDate.text = if (track.releaseDate == null) placeholderString else {
            SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
        }
        collectionName.text = track.collectionName ?: placeholderString
        primaryGenreName.text = track.primaryGenreName ?: placeholderString
        country.text = track.country ?: placeholderString
        currentTrackTime.text = updateCurrentTrackTime()

        preparePlayer(track.previewUrl ?: "")

        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)
        val likeButton = findViewById<ToggleButton>(R.id.like_button)
        play = findViewById(R.id.play_button)
        val addButton = findViewById<ImageButton>(R.id.add_button)

        mainThreadHandler = Handler(Looper.getMainLooper())

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        likeButton.setOnCheckedChangeListener { _, isChecked ->

        }

        play.setOnClickListener {
            playbackControl()
        }

        addButton.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            this.resources.displayMetrics
        ).toInt()
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacksAndMessages(null)
            currentTrackTimeInMillis = 0L
            currentTrackTime.text = updateCurrentTrackTime()
            play.isSelected = false
            playerState = STATE_PREPARED

        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.isSelected = true
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.isSelected = false
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        val startTime = System.currentTimeMillis()
        mainThreadHandler?.post(
            createUpdateTimerTask(startTime, currentTrackTimeInMillis)
        )
    }

    private fun createUpdateTimerTask(startTime: Long, currentTime: Long): Runnable {
        return object : Runnable {
            override fun run() {

                val elapsedTime = System.currentTimeMillis() - startTime

                if (playerState == STATE_PLAYING) {
                    currentTrackTimeInMillis = currentTime + elapsedTime
                    currentTrackTime.text = updateCurrentTrackTime()
                    mainThreadHandler?.postDelayed(this, DELAY)
                }
            }
        }
    }

    private fun updateCurrentTrackTime() : String {
        return TrackTimePeriod.fromMillis(currentTrackTimeInMillis).toString()
    }
}
