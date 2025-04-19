package com.example.playlistmaker.player.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.TrackTimePeriod
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.TrackUiState
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.common.ui.castom_view.CustomCircularProgressIndicator
import com.example.playlistmaker.common.ui.castom_view.CustomToast
import com.example.playlistmaker.common.ui.recycler_components.playlist.PlaylistAdapter
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.new_playlist.ui.NewPlaylistActivity
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.presentation.model.PlayerPropertyState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.LinkedList
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var progressBar: CustomCircularProgressIndicator
    private var adapter = PlaylistAdapter(PlaylistAdapter.ITEM_VIEW_ROW)
    private var progressBarId: Int? = null
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val trackIdBundle = intent?.extras

        if (trackIdBundle != null) {
            viewModel = getViewModel { parametersOf(trackIdBundle) }
        } else {
            onBackPressedDispatcher.onBackPressed()
        }

        val bottomSheetContainer = binding.standardBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val dimOverlay = binding.dimOverlay

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            showRender(screenState)
        }

        viewModel.getPlayStatusLiveData().observe(this) { playerPropertyState ->
            changeButtonPlay(playerPropertyState)
            updateTimer(playerPropertyState)
        }


        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.topToolbarFrame.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        binding.likeButton.setOnClickListener {
            viewModel.favoriteControl(binding.likeButton.isChecked)
        }

        binding.addButton.setOnClickListener {

            dimOverlay.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        //    viewModel.getPlaylists()
        }

        binding.newPlaylistButton.setOnClickListener {
            val intent = Intent(this, NewPlaylistActivity::class.java)
            startActivity(intent)
        }

        adapter.setOnItemClickListener = { playlistPair ->

            if (clickDebounce()) {
                if (playlistPair is ItemPlaylistWrapper.PlaylistPair) {
                    viewModel.addTrack(playlistPair)

                }
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                        dimOverlay.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        viewModel.getPlaylists()
                    else -> {
                        // empty
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // empty
            }
        })
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

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer()
    }

    private fun <T : ItemPlaylistWrapper.PlaylistPair> showRender(screenState: ListUiState<T>) {
        when (screenState) {
            ListUiState.Loading -> changeContentVisibility(loading = true)
            is TrackUiState.LoadTrack -> showTrackView(screenState.trackModel)
            is TrackUiState.ToastMessage -> showToast(screenState.message)
            is ListUiState.Content<T> -> activateAddPanel(
                LinkedList(
                    screenState.contentList
                )
            )
        }
    }

    private fun showTrackView(track: Track) {

        changeContentVisibility(loading = false)

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
        binding.currentTrackTime.text = TrackTimePeriod.fromMillis(0L).toString().trim()
        binding.likeButton.isChecked = track.isFavorite
    }

    private fun changeContentVisibility(loading: Boolean) {

        var visibilityView = View.GONE
        var unvisibilityView = View.GONE

        if (loading) {
            progressBar = CustomCircularProgressIndicator(this).apply {
                id = View.generateViewId()
                this@PlayerActivity.progressBarId = id
                this.layoutParams = CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
            }
            binding.root.addView(progressBar)
            visibilityView = View.GONE
            unvisibilityView = View.VISIBLE
        } else {
            visibilityView = View.VISIBLE
            unvisibilityView = View.GONE
        }

        if (progressBarId != null) {
            progressBar.visibility = unvisibilityView
        }
        binding.playerActivity.visibility = visibilityView
    }

    private fun activateAddPanel(playlists: LinkedList<ItemPlaylistWrapper.PlaylistPair>) {

        val recyclerView = binding.playlistsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.updateList(playlists)
    }

    private fun clickDebounce(): Boolean {

        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true

            }
        }
        return current
    }

    private fun showToast(message: String) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        CustomToast(this, binding.root)
            .setMessage(message)
            .show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
