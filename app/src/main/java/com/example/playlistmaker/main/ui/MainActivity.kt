package com.example.playlistmaker.main.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.App
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.common.presentation.model.TopicalFragment
import com.example.playlistmaker.common.ui.model.MutableInsets
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity() : AppCompatActivity() {

    private lateinit var app: App
    private lateinit var binding: ActivityMainBinding
    private lateinit var statusBarsInsets: Insets
    private val viewModel: MainViewModel by viewModel()
    private val darkThemeViewModel: DarkThemeViewModel by viewModel()
    private var changeModeListener: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        app = application as App
        ScreenSize.initSize(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val paddingBottom =
                if (ime.bottom > 0) {
                    ime.bottom
                } else {
                    0
                }

            view.setPadding(
                statusBarsInsets.left, statusBarsInsets.top, statusBarsInsets.right,
                paddingBottom
            )
            insets
        }

        darkThemeViewModel.getDarkThemeLiveData().observe(this) {
            window.decorView.systemUiVisibility = systemUiVisibility(it.first)

            if (it.second != null) {
                changeModeListener = it.first
                app.switchTheme(it.first)
                if (it.first) {
                    setTheme(R.style.Base_Theme_PlaylistMaker_Dark)
                } else {
                    setTheme(R.style.Base_Theme_PlaylistMaker_Light)
                }
                changeModeListener = null
            }
        }

        viewModel.getTopicalFragmentLiveData().observe(this) {state ->
            insetsStateRender(state)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun recreate() {

        if (changeModeListener == null) {
            super.recreate()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        viewModel.focusChange(hasFocus)
    }

    private fun systemUiVisibility(isLightBackground: Boolean): Int {
        return if (isLightBackground) 0 else
            (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }

    private fun recalculationInsets(state: TopicalFragment, imeVisible: Boolean, mutableSystemBars: MutableInsets,
                       function: (mutableBars: MutableInsets) -> Insets) : Insets {

        if (imeVisible || state == TopicalFragment.PLAYLIST) {
            binding.bottomNavigationView.visibility = View.GONE
            binding.lineView.visibility = View.GONE
        } else {
            binding.bottomNavigationView.visibility = View.VISIBLE
            binding.lineView.visibility = View.VISIBLE
        }

        when (state) {
            TopicalFragment.DEFAULT ->
                mutableSystemBars.bottom = 0
            TopicalFragment.SETTING ->
                mutableSystemBars.bottom = 0
            TopicalFragment.LIBRARY ->
                mutableSystemBars.bottom = 0
            TopicalFragment.PLAYLIST ->
                mutableSystemBars.top = 0
            TopicalFragment.SEARCH ->
                mutableSystemBars.bottom = 0
        }
        return function(mutableSystemBars)
    }

    private fun insetsStateRender(state : TopicalFragment,) {

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val mutableSystemBars = MutableInsets(
                systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom)
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val newInsets = recalculationInsets(state, imeVisible, mutableSystemBars ) {

                if (imeVisible) it.bottom = ime.bottom
                return@recalculationInsets it.toInsets()
            }
            view.setPadding(
                newInsets.left, newInsets.top, newInsets.right,
                newInsets.bottom
            )
            insets
        }
    }
}
