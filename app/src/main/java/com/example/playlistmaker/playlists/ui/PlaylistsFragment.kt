package com.example.playlistmaker.playlists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.common.ui.fragments.IncludeFragment
import com.example.playlistmaker.common.ui.recycler_components.playlist.PlaylistAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.playlists.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment() : IncludeFragment<FragmentPlaylistsBinding, ItemPlaylistWrapper>() {

    companion object {
        fun newInstance() = PlaylistsFragment(
        )
    }

    private val viewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()

    override val navigateIdAction: Int= R.id.action_favoriteTracksFragment_to_playerActivity
    override val adapter: PlaylistAdapter = PlaylistAdapter(PlaylistAdapter.ITEM_VIEW_RECTANGLE)
    override val extraActionBundleKey: ExtraActionBundleKey = ExtraActionBundleKey.PLAYLIST_EXTRA

    override fun createBinding(
        increateBindingflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return  FragmentPlaylistsBinding.inflate(increateBindingflater, container, false)
    }

    override fun renderUiIncludeState(state: ListUiState.ListUiIncludeState<ItemPlaylistWrapper>) {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState.observe(viewLifecycleOwner) {
            showContentRender(it)
        }

        adapter.setOnItemClickListener = { playlist ->
            viewModel.del(playlist.playlist)
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistActivity)
        }
    }

    override fun initListTracksView() {
        super.initListTracksView()
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, )

    }

    override fun updateIncludeViewByProgressBar() {
        super.updateIncludeViewByProgressBar()
        binding.placeholderMessage.visibility = View.GONE
    }

    override fun updateIncludeViewByList(list: List<ItemPlaylistWrapper>) {
        super.updateIncludeViewByList(list)
        binding.placeholderMessage.visibility = View.GONE
    }

    override fun updateIncludeViewByClear() {
        super.updateIncludeViewByClear()
        binding.placeholderMessage.visibility = View.VISIBLE
    }

    override fun goToIntent(entityId: Int) {
        viewModel.restoreState()
    }
}