package com.example.playlistmaker.favorite_tracks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.common.ui.fragments.IncludeFragment
import com.example.playlistmaker.common.ui.recycler_components.track.TrackAdapter
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.favorite_tracks.presentation.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment() : IncludeFragment<FragmentFavoriteTracksBinding, Track>() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    override val extraActionBundleKey: ExtraActionBundleKey =
        ExtraActionBundleKey.TRACK_EXTRA_FAVORITE
    override val navigateIdAction: Int = R.id.action_favoriteTracksFragment_to_playerActivity
    override val adapter: TrackAdapter = TrackAdapter()
    private val viewModel by viewModel<FavoriteTracksViewModel>()

    override fun createBinding(
        increateBindingflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(increateBindingflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState.observe(viewLifecycleOwner) {
            showContentRender(it)
        }

        adapter.setOnItemClickListener = { track ->
            viewModel.showTrack(track)
        }
    }

    override fun goToIntent(entityId: Int) {
        super.goToIntent(entityId)
        viewModel.restoreState()
    }

    override fun updateIncludeViewByList(list: List<Track>) {
        super.updateIncludeViewByList(list)
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

    override fun renderUiIncludeState(state: ListUiState.ListUiIncludeState<Track>) {
        //Empty
    }
}
