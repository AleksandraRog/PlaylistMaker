package com.example.playlistmaker


import android.os.Bundle
import android.util.Log
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


        Glide.with(this)
            .load(track.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")).centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f)))
            .into(imageView)
        val placeholderString = this.getString(R.string.no_reply)
        trackName.text = track.trackName ?: placeholderString
        artistName.text = track.artistName ?: placeholderString
        trackTime.text = track.trackTime?.toString() ?: placeholderString
        Log.d("setBackground", "track.releaseDate ${track.releaseDate}")

        releaseDate.text = if (track.releaseDate == null) placeholderString else {
        val simpleDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            simpleDateFormat.format(track.releaseDate) }
        collectionName.text = track.collectionName ?: placeholderString
        primaryGenreName.text = track.primaryGenreName ?: placeholderString
        country.text = track.country ?: placeholderString


        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)
        val likeButton = findViewById<ToggleButton>(R.id.like_button)
        val playButton = findViewById<ImageView>(R.id.play_button)
        val addButton = findViewById<ImageButton>(R.id.add_button)

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        likeButton.setOnCheckedChangeListener {_, isChecked ->

        }

        playButton.setOnClickListener {

        }

        addButton.setOnClickListener {

        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            this.resources.displayMetrics
        ).toInt()
    }
}
