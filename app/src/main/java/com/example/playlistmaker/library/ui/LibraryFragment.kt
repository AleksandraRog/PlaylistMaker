package com.example.playlistmaker.library.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.example.playlistmaker.library.presentation.LibraryViewPagerAdapter
import com.example.playlistmaker.library.presentation.PagerState
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    private var isSmoothScrollEnabled = false
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding =  ActivityLibraryBinding.inflate(inflater, container, false)
        Log.d("MainActivity","LibraryFragment ${3}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MainActivity","LibraryFragment ${4}")
        binding.viewPager.adapter = LibraryViewPagerAdapter(
            childFragmentManager,
            lifecycle
        )
        Log.d("MainActivity","LibraryFragment ${5}")
//        val
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            tab.text = PagerState.entries.find { it.number == position }?.titleName
        }
        Log.d("MainActivity","LibraryFragment ${6}")
        tabMediator.attach()
        Log.d("MainActivity","LibraryFragment ${7}")
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.saveTab(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        Log.d("MainActivity","LibraryFragment ${8}")
        viewModel.observeState().observe(viewLifecycleOwner) {
            if (!isSmoothScrollEnabled) {
                binding.viewPager.setCurrentItem(it.number + 1, true)
                isSmoothScrollEnabled = true
            }
            binding.tabLayout.getTabAt(it.number)?.select()
        }
        Log.d("MainActivity","LibraryFragment ${9}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity","LibraryFragment ${1}")


    }


    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}
