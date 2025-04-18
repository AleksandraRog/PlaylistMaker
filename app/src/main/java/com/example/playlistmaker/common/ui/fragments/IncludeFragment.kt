package com.example.playlistmaker.common.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import com.example.playlistmaker.common.ui.castom_view.CustomCircularProgressIndicator
import com.example.playlistmaker.common.ui.recycler_components.common.RecyclerAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class IncludeFragment<T : ViewBinding, I,> : Fragment() {

    lateinit var includeView: ViewGroup
    lateinit var recyclerView: RecyclerView
    private val progressBar: ProgressBar by lazy { CustomCircularProgressIndicator(requireContext()) }
    abstract val adapter: RecyclerAdapter<I>
    private var isClickAllowed = true
    lateinit var binding: T

    abstract val extraActionBundleKey: ExtraActionBundleKey
    abstract val navigateIdAction: Int

    abstract fun createBinding(increateBindingflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenSize.initSizeTrackViewHolder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = createBinding(inflater, container)
        includeView = binding.root.findViewById(R.id.includeView)
        return binding.root
    }

    fun showContentRender(state: ListUiState<I>) {
        when (state) {
            is ListUiState.AnyItem -> goToIntent(state.itemId)
            is ListUiState.Content -> updateIncludeViewByList(state.contentList)
            ListUiState.Default -> updateIncludeViewByClear()
            ListUiState.Empty -> updateIncludeViewByEmpty()
            ListUiState.Loading -> updateIncludeViewByProgressBar()
            is ListUiState.ListUiIncludeState -> renderUiIncludeState(state)
        }
    }

    open fun goToIntent(entityId: Int) {

        val bundleTrackId = Bundle().apply {
            putInt(extraActionBundleKey.value, entityId)

        }

        findNavController().navigate(navigateIdAction, bundleTrackId)

    }

    open fun initListTracksView() {

        recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = SizeFormatter.dpToPx(16f, context)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    fun clickDebounce(): Boolean {

        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    open fun updateIncludeViewByProgressBar() {
        updateIncludeView(progressBar)
    }

    open fun updateIncludeViewByList(list: List<I>) {
        initListTracksView()
        updateIncludeView(recyclerView)
        adapter.updateList(list)
    }

    open fun updateIncludeViewByClear() {
        includeView.removeAllViews()
    }

    open fun updateIncludeViewByEmpty() {
        updateIncludeViewByClear()
    }

    fun updateIncludeView(includedView: View?) {
        includeView.removeAllViews()
        includeView.addView(includedView)
    }

    abstract fun renderUiIncludeState(state: ListUiState.ListUiIncludeState<I>)

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
