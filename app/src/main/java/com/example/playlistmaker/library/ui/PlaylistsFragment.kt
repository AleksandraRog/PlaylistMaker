package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.presentation.PlaylistsViewModel

class PlaylistsFragment : Fragment() {


    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private val viewModel: PlaylistsViewModel by viewModels()
    private lateinit var binding: FragmentPlaylistsBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner){
            showContent(it)
        }

        binding.newPlaylistButton.setOnClickListener {

        }
    }

    fun showContent(flag: Boolean){
        if (flag) {
            binding.placeholderMessage.visibility = View.GONE
            binding.playlistsRecyclerview.visibility = View.VISIBLE
        } else {
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.playlistsRecyclerview.visibility = View.GONE
        }
    }



}