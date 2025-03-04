package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.TrackTimePeriod
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import com.example.playlistmaker.common.ui.CustomCircularProgressIndicator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.presentation.PlayerPropertyState
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.presentation.TrackScreenState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.search.ui.SearchFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    private lateinit var progressBar: CustomCircularProgressIndicator
    var progressBarId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackId = intent?.extras?.getInt(SearchFragment.TRACK_EXTRA, -1) ?: -1

        if (trackId == -1) { onBackPressedDispatcher.onBackPressed()}

        viewModel = getViewModel{ parametersOf (trackId) }


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

        viewModel.getScreenStateLiveData().observe(this) { screenState ->

            when (screenState) {
                is TrackScreenState.Content -> {
                    changeContentVisibility(loading = false)
                    showTrackView(screenState.trackModel)
                }

                is TrackScreenState.Loading -> {
                    changeContentVisibility(loading = true)
                }
            }

            viewModel.getPlayStatusLiveData().observe(this) { playerPropertyState ->
                changeButtonPlay(playerPropertyState)
                updateTimer(playerPropertyState)
            }
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.topToolbarFrame.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        binding.likeButton.setOnCheckedChangeListener { _, isChecked ->

        }

        binding.addButton.setOnClickListener {

        }

    }

    private fun updateTimer(playerPropertyState: PlayerPropertyState) {
        binding.currentTrackTime.text =
            TrackTimePeriod.fromMillis(playerPropertyState.timer).toString()
    }

    private fun changeButtonPlay(playerPropertyState: PlayerPropertyState) {
        when (playerPropertyState.playerState) {
            PlayerState.STATE_PREPARED -> {
                binding.playButton.isEnabled = true
                binding.playButton.isSelected = false
            }

            PlayerState.STATE_PLAYING -> {
                binding.playButton.isSelected = true
            }

            PlayerState.STATE_PAUSED -> {
                binding.playButton.isSelected = false
            }

            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun showTrackView(track: Track) {

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(SizeFormatter.dpToPx(8f, this)))
            .into(binding.trackImage)
        val placeholderString = this.getString(R.string.no_reply)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTime?.toString() ?: placeholderString

        binding.releaseDate.text = if (track.releaseDate == null) placeholderString else {
            SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
        }
        binding.collectionName.text = track.collectionName
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country
        binding.currentTrackTime.text = TrackTimePeriod.fromMillis(0L).toString()
    }

    private fun changeContentVisibility(loading: Boolean) {

        var visibilityView = View.GONE
        var unvisibilityView = View.GONE

        if (loading) {
            progressBar = CustomCircularProgressIndicator(this).apply {
                id = View.generateViewId()
                this@PlayerActivity.progressBarId = id
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val constraintSetProgressBar = ConstraintSet()
            constraintSetProgressBar.clone(binding.root)
            constraintSetProgressBar.connect(
                progressBar.id,
                ConstraintSet.TOP,
                binding.root.id,
                ConstraintSet.TOP
            )
            constraintSetProgressBar.connect(
                progressBar.id,
                ConstraintSet.BOTTOM,
                binding.root.id,
                ConstraintSet.BOTTOM
            )
            constraintSetProgressBar.connect(
                progressBar.id,
                ConstraintSet.START,
                binding.root.id,
                ConstraintSet.START
            )
            constraintSetProgressBar.connect(
                progressBar.id,
                ConstraintSet.END,
                binding.root.id,
                ConstraintSet.END
            )
            constraintSetProgressBar.applyTo(binding.root)
            binding.root.addView(progressBar)

            val constraintSetToolbar = ConstraintSet()
            constraintSetToolbar.clone(binding.root)
            constraintSetToolbar.clear(binding.topToolbarFrame.id, ConstraintSet.BOTTOM)
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.TOP,
                binding.root.id,
                ConstraintSet.TOP
            )
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.START,
                binding.root.id,
                ConstraintSet.START
            )
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.END,
                binding.root.id,
                ConstraintSet.END
            )
            constraintSetToolbar.applyTo(binding.root)
            visibilityView = View.GONE
            unvisibilityView = View.VISIBLE
        } else if (progressBarId!=null){

            progressBar = findViewById(progressBarId!!)
            visibilityView = View.VISIBLE
            unvisibilityView = View.GONE
            val constraintSetToolbar = ConstraintSet()
            constraintSetToolbar.clone(binding.root)
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.TOP,
                binding.root.id,
                ConstraintSet.TOP
            )
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.START,
                binding.root.id,
                ConstraintSet.START
            )
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.END,
                binding.root.id,
                ConstraintSet.END
            )
            constraintSetToolbar.connect(
                binding.topToolbarFrame.id,
                ConstraintSet.BOTTOM,
                binding.trackImage.id,
                ConstraintSet.TOP
            )
            constraintSetToolbar.applyTo(binding.root)

            binding.root.removeView(progressBar)
        }

        for (i in 0 until (binding.root as ViewGroup).childCount) {
            val view = (binding.root as ViewGroup).getChildAt(i)
            if (view.id != progressBar.id) {
                view.visibility = visibilityView
            } else view.visibility = unvisibilityView
        }
    }


}
