package com.example.playlistmaker.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.mapper.TextFormForIntFormatter
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.common.presentation.model.SharingObjects
import com.example.playlistmaker.common.presentation.model.TopicalFragment
import com.example.playlistmaker.common.ui.castom_view.CustomCircularProgressIndicator
import com.example.playlistmaker.common.ui.castom_view.CustomToast
import com.example.playlistmaker.common.ui.fragments.IncludeFragment
import com.example.playlistmaker.common.ui.model.AlertDialogParameters
import com.example.playlistmaker.common.ui.recycler_components.common.RecyclerAdapter
import com.example.playlistmaker.common.ui.recycler_components.track.TrackAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.databinding.PlaylistRowViewBinding
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.playlist.presentation.PlaylistUiState
import com.example.playlistmaker.playlist.presentation.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : IncludeFragment<FragmentPlaylistBinding, Track>() {

    override val adapter: RecyclerAdapter<Track> = TrackAdapter()
    override val extraActionBundleKey: ExtraActionBundleKey =
        ExtraActionBundleKey.TRACK_EXTRA_PLAYLIST
    override val navigateIdAction: Int = R.id.action_playlistFragment_to_playerActivity

    private lateinit var viewModel: PlaylistViewModel
    private lateinit var progressBar: CustomCircularProgressIndicator
    private lateinit var bottomSheetBehaviorTrack: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetBehaviorMenu: BottomSheetBehavior<LinearLayout>
    private lateinit var shortInfoBinding: PlaylistRowViewBinding

    private var progressBarId: Int? = null
    private var baseGuidelineHeight = 0
    private var basePeekHeight = 0
    private val mainViewModel: MainViewModel by activityViewModel()
    private val args: PlaylistFragmentArgs by navArgs()

    override fun createBinding(
        increateBindingflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        mainViewModel.setFragmentScreen(TopicalFragment.PLAYLIST)

        return FragmentPlaylistBinding.inflate(increateBindingflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shortInfoBinding = PlaylistRowViewBinding.bind(binding.navigationView.getHeaderView(0))
        baseGuidelineHeight = ViewCompat.getRootWindowInsets(requireView())
            ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0

        val topToolbarParams = binding.topToolbarFrame.layoutParams as ConstraintLayout.LayoutParams
        binding.topToolbarFrame.layoutParams =
            topToolbarParams.apply { topMargin = baseGuidelineHeight }

        viewModel = getViewModel { parametersOf(args.playlistId) }

        binding.topToolbarFrame.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.observeState.observe(viewLifecycleOwner) { screenState ->

            if (screenState is ListUiState.Content) {
                val previousScreenStateLiveData = viewModel.getPreviousScreenStateLiveData().value

                if (previousScreenStateLiveData is PlaylistUiState.LoadPlaylist) {
                    showContentRender(previousScreenStateLiveData)
                }
            }
            showContentRender(screenState)
        }

        bottomSheetBehaviorMenu = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehaviorMenu.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.tracksBottomSheet.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_EXPANDED ->
                        binding.tracksBottomSheet.visibility = View.GONE

                    else -> {
                        // empty
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.tracksBottomSheet.visibility = View.VISIBLE
            }
        })

        adapter.setOnItemClickListener = { track ->
            viewModel.showAction(track)
        }

        adapter.setOnItemLongClickListener = { track ->
            AlertDialogParameters(
                title = getString(R.string.delete_track),
                message = getString(R.string.question_delete_track),
                neutralButtonTitle = getString(R.string.comeback),
                positiveButtonTitle = getString(R.string.delete)
            )
                .positiveButtonAction {
                    viewModel.deleteTrack(track)
                }
                .activateAlertDialog(requireContext())
                .show()
            true
        }

        binding.bottomToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionSharing -> {
                    viewModel.getIntentProperty()
                    true
                }

                R.id.action_more -> {
                    viewModel.showBottomSheetMenu()
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionSharing -> {
                    viewModel.getIntentProperty()
                    true
                }

                R.id.actionDelete -> {

                    AlertDialogParameters(
                        title = getString(R.string.delete_playlist),
                        message = getString(R.string.question_delete_playlist),
                        negativeButtonTitle = getString(R.string.not),
                        positiveButtonTitle = getString(R.string.yes)
                    )
                        .positiveButtonAction {
                            viewModel.deletePlaylist()
                        }
                        .activateAlertDialog(requireContext())
                        .show()
                    bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
                    true
                }

                R.id.actionEdit -> {
                    viewModel.prepareActionToEditPlaylist()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun renderUiIncludeState(state: ListUiState.ListUiIncludeState<Track>) {
        when (state) {
            is PlaylistUiState.LoadPlaylist -> showPlaylistView(state.playlistModel)
            is PlaylistUiState.SharePlaylist -> intentSharing(state.sharingObj)
            is PlaylistUiState.ExpandBottomSheetMenu -> showBottomSheetMenu(state.playlist)
            is PlaylistUiState.EditPlaylist -> goToNewPlaylistActivity(state.playlist)
            PlaylistUiState.DeletePlaylist -> closeFragment()
            PlaylistUiState.ShowToastEmpty -> {
                bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
                showToast(getString(R.string.empty_playlist_for_sharing))
            }
        }
    }

    private fun goToNewPlaylistActivity(playlist: Playlist) {
        val bundleEntityId = Bundle().apply {
            putInt(ExtraActionBundleKey.PLAYLIST_EXTRA_PLAYLIST.value, playlist.playlistId)
        }
        findNavController().navigate(
            R.id.action_playlistFragment_to_newPlaylistActivity,
            bundleEntityId
        )
    }

    private fun closeFragment() {
        findNavController().popBackStack()
    }

    private fun intentSharing(sharingObj: SharingObjects) {

        bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
        if (sharingObj.intent is Intent) {
            requireActivity().startActivity(sharingObj.intent as Intent)
        }
    }

    private fun showPlaylistView(playlist: Playlist) {

        changeContentVisibility(loading = false)

        Glide.with(this)
            .load(playlist.fileUrl)
            .centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .into(binding.playlistImage)
        binding.playlistName.text = playlist.playlistName

        binding.playlistDescription.apply {
            text = playlist.description
            visibility = if (playlist.description.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.playlistTime.text = TextFormForIntFormatter.pluralFormMinuteCount(
            playlist.playlistTime?.duration?.toMinutes() ?: 0L
        )
        binding.playlistCount.text =
            TextFormForIntFormatter.pluralFormTrackCount(playlist.playlistSize)

        binding.playlistDescription.post {
            basePeekHeight = ScreenSize.getScreenHeight() - binding.bottomToolbar.bottom +
                    baseGuidelineHeight

        }
        initBottomSheetBehavior(binding.bottomToolbar, binding.tracksBottomSheet){

            bottomSheetBehaviorTrack = it
        }
        initBottomSheetBehavior(binding.playlistName, binding.menuBottomSheet,){
            bottomSheetBehaviorMenu = it.apply { maxHeight =  ScreenSize.getScreenHeight() - binding.playlistName.bottom + baseGuidelineHeight}
        }
    }

    private fun changeContentVisibility(loading: Boolean) {

        var visibilityView = View.GONE
        var unvisibilityView = View.GONE

        if (loading) {
            progressBar = CustomCircularProgressIndicator(requireContext()).apply {
                id = View.generateViewId()
                this@PlaylistFragment.progressBarId = id
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
        binding.playlistActivity.visibility = visibilityView
    }

    private fun showBottomSheetMenu(playlist: Playlist) {

        bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_EXPANDED

        Glide.with(this)
            .load(playlist.fileUrl)
            .centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .into(shortInfoBinding.playlistImage)
        shortInfoBinding.playlistName.text = binding.playlistName.text
        shortInfoBinding.playlistSize.text = binding.playlistCount.text
    }

    override fun goToIntent(entityId: Int) {
        super.goToIntent(entityId)
        viewModel.restoreState()
    }

    override fun updateIncludeViewByEmpty() {
        super.updateIncludeViewByEmpty()
        bottomSheetBehaviorTrack.isHideable = true
        bottomSheetBehaviorTrack.state = BottomSheetBehavior.STATE_HIDDEN
        showToast(getString(R.string.without_tacks))
    }

    private fun showToast(message: String) {
        CustomToast(requireContext(), binding.root)
            .setMessage(message)
            .show()
    }

    private fun <V : ViewGroup> initBottomSheetBehavior(view: View,  bottomSheetView : V, onReady: (BottomSheetBehavior<V>) -> Unit) {

        view.post {
            val basePeekHeight = ScreenSize.getScreenHeight() - view.bottom +
                    baseGuidelineHeight
            val bottomSheetBehavior =
                BottomSheetBehavior.from(bottomSheetView).apply {
                    peekHeight = basePeekHeight
                    state = BottomSheetBehavior.STATE_HIDDEN
                }

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        else -> {
                            // empty
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // empty
                }
            })
            onReady(bottomSheetBehavior)

        }
    }
}