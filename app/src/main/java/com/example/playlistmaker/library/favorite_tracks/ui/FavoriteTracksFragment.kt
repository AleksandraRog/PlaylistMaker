package com.example.playlistmaker.library.favorite_tracks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.ExtraActionBundleKey
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.UiState
import com.example.playlistmaker.common.ui.IncludeFragment
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.library.favorite_tracks.presentation.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment() : IncludeFragment<FragmentFavoriteTracksBinding>() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    override val extraActionBundleKey: ExtraActionBundleKey = ExtraActionBundleKey.TRACK_EXTRA_FAVORITE
    override val navigateIdAction: Int = R.id.action_favoriteTracksFragment_to_playerActivity
    private val viewModel by viewModel<FavoriteTracksViewModel>()

    override fun createBinding(increateBindingflater: LayoutInflater,
        container: ViewGroup?): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(increateBindingflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState.observe(viewLifecycleOwner) {
            showContentRender(it)
        }

        trackAdapter.setOnItemClickListener = { track ->
            if (clickDebounce()) {
                viewModel.showTrack(track)
            }
        }
    }

    override fun goToPlayerActivity(trackId: Int) {
        super.goToPlayerActivity(trackId)
        viewModel.restoreState()
    }

    override fun updateIncludeViewByTrackList(tracks: List<Track>) {
        super.updateIncludeViewByTrackList(tracks)
         binding.placeholderMessage.visibility = View.GONE
    }

    override fun updateIncludeViewByClear() {
        super.updateIncludeViewByClear()
        binding.placeholderMessage.visibility = View.VISIBLE
    }

    override fun updateIncludeViewByProgressBar() {
        super.updateIncludeViewByProgressBar()
        binding.placeholderMessage.visibility = View.GONE
    }

    override fun renderUiIncludeState(state: UiState.UiIncludeState) {
        //Empty
    }
}
