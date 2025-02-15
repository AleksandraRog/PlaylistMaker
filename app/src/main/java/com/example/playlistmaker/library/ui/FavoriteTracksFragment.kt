package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.library.presentation.FavoriteTracksViewModel

class FavoriteTracksFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    private lateinit var binding: FragmentFavoriteTracksBinding
    private val viewModel: FavoriteTracksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {

            showContent(it)
        }
    }

    fun showContent(flag: Boolean){
        if (flag) {
            binding.placeholderMessage.visibility = View.GONE
            binding.favoriteTracksRecyclerview.visibility = View.VISIBLE
        } else {
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.favoriteTracksRecyclerview.visibility = View.GONE
        }
    }
}