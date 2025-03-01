package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import com.example.playlistmaker.sharing.presentation.SharingObjects
import com.example.playlistmaker.sharing.presentation.SharingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: ActivitySettingsBinding
    private val sharingsViewModel: SharingsViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()
    private val darkThemeViewModel: DarkThemeViewModel by activityViewModel()

    private var intentValue: Intent? = null
    private var isDarkMode: Boolean? = null
    private var changeModeListener: SharingObjects? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharingsViewModel.getSharingObjectLiveData().observe(viewLifecycleOwner) {

            if (it.intent is Intent) {
                intentValue = it.intent as Intent
                requireActivity().startActivity(intentValue)
            }
            changeModeListener = it
        }

        darkThemeViewModel.getDarkThemeLiveData().observe(viewLifecycleOwner) {

            binding.switchTheme.isChecked = it.first
            isDarkMode = it.first

            if (it.second != null && it.second !=it.first) {
                reColors(requireActivity().window.decorView as ViewGroup)
                requireActivity().window.decorView.systemUiVisibility = systemUiVisibility(it.first)
                darkThemeViewModel.configPreviousDarkTheme(it.first)
           }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            darkThemeViewModel.configDarkTheme(isChecked)
        }

        binding.buttonSupport.setOnClickListener {
            sharingsViewModel.openSupport()
        }

        binding.buttonShare.setOnClickListener {
            sharingsViewModel.shareApp()
        }

        binding.buttonArrow.setOnClickListener {
            sharingsViewModel.openTerms()
        }

        mainViewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { hasFocus ->
            lifecycleObserver(hasFocus)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ActivitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity","SettingsFragment ${1}")
    }

    private fun lifecycleObserver(hasFocus: Boolean) {
        val currentNightMode = isDarkMode
        val requireContext = requireContext()
        if (hasFocus) {
            if (currentNightMode == true) {
                binding.buttonSupport.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_white
                    )
                )
                binding.buttonArrow.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_white
                    )
                )
                binding.buttonShare.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_white
                    )
                )
            } else {
                binding.buttonSupport.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_text_grey
                    )
                )
                binding.buttonArrow.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_text_grey
                    )
                )
                binding.buttonShare.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_text_grey
                    )
                )
            }

            sharingsViewModel.restoreState()

        } else if (currentNightMode == false) {
            when (changeModeListener) {
                SharingObjects.SUPPORT -> binding.buttonSupport.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_black
                    )
                )

                SharingObjects.SHARE_APP -> binding.buttonShare.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_black
                    )
                )

                SharingObjects.TERMS -> binding.buttonArrow.setColorFilter(
                    ContextCompat.getColor(
                        requireContext,
                        R.color.YP_black
                    )
                )

                else -> TODO()
            }
        }
    }

    private fun reColors(viewGroup: ViewGroup) {

        val requireContext = requireContext()
        val iconColor = if (isDarkMode == true)
            ContextCompat.getColor(requireContext, R.color.YP_white) else
            ContextCompat.getColor(requireContext, R.color.YP_text_grey)
        val textColor = if (isDarkMode == true)
            ContextCompat.getColor(requireContext, R.color.YP_white) else
            ContextCompat.getColor(requireContext, R.color.YP_black)
        val backgroundColor = if (isDarkMode == true)
            ContextCompat.getColor(requireContext, R.color.YP_black) else
            ContextCompat.getColor(requireContext, R.color.YP_white)
        val navigationBarColor = if (isDarkMode == true)
            ContextCompat.getColor(requireContext, R.color.black) else
            ContextCompat.getColor(requireContext, R.color.YP_white)

        requireActivity().window.statusBarColor = backgroundColor
        requireActivity().window.navigationBarColor = navigationBarColor

          for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            when (view) {

                is BottomNavigationView -> {

                    val colorStateList = view.itemIconTintList
                    val selectedTint = colorStateList?.getColorForState(intArrayOf(android.R.attr.state_checked), textColor) ?: textColor

                     val newColorStateList = ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(selectedTint, textColor)
                    )

                    view.itemIconTintList = newColorStateList
                    view.itemTextColor = newColorStateList
                    view.setBackgroundColor(backgroundColor)
                }



                is TextView -> {
                    view.setTextColor(textColor)
                    view.setBackgroundColor(backgroundColor)
                }

                is ImageView -> {
                    if (view is ImageButton) {
                        view.setColorFilter(textColor)
                    } else {
                        view.setColorFilter(iconColor)
                    }
                    view.setBackgroundColor(backgroundColor)
                }
                is ViewGroup -> {
                    if(view.id != R.id.lineView){

                        view.setBackgroundColor(backgroundColor)
                        reColors(view)
                    }
                }

            }
        }
    }

    private fun systemUiVisibility(isLightBackground: Boolean): Int {
        return if (isLightBackground) 0 else
            (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }
}
