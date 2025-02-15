package com.example.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryViewModel
import com.example.playlistmaker.library.presentation.LibraryViewPagerAdapter
import com.example.playlistmaker.library.presentation.PagerState
import com.example.playlistmaker.main.ui.MainActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    private var isSmoothScrollEnabled = false
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom
            )
            insets
        }

        setSupportActionBar(binding.topToolbarFrame)

        binding.topToolbarFrame.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.viewPager.adapter = LibraryViewPagerAdapter(
            supportFragmentManager,
            lifecycle
        )
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            tab.text = PagerState.entries.find { it.number == position }?.titleName
        }

        tabMediator.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.saveTab(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.observeState().observe(this) {
            if (!isSmoothScrollEnabled) {
                binding.viewPager.setCurrentItem(it.number + 1 , true)
                isSmoothScrollEnabled = true
            }
           binding.tabLayout.getTabAt(it.number)?.select()
        }

        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val mainIntent = Intent(this@LibraryActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}
