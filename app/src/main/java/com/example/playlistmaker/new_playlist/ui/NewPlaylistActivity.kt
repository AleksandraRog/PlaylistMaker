package com.example.playlistmaker.new_playlist.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.canhub.cropper.CropImageView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import com.example.playlistmaker.common.ui.castom_view.CustomToast
import com.example.playlistmaker.databinding.ActivityNewPlaylistBinding
import com.example.playlistmaker.new_playlist.presentation.CreatePlaylistScreenState
import com.example.playlistmaker.new_playlist.presentation.MakePlaylistViewModel
import com.example.playlistmaker.search.ui.SearchFragment.Companion.EDIT_TEXT_DEF
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistActivity : AppCompatActivity() {

    private val viewModel: MakePlaylistViewModel by viewModel()
    private lateinit var binding: ActivityNewPlaylistBinding
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher
    private lateinit var playlist: Playlist
    private lateinit var cropImageView: CropImageView
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var deleteFlag = false
    private var nameTextValue: String = EDIT_TEXT_DEF
    private var descriptionTextValue: String = EDIT_TEXT_DEF
    private var fileUrlTextValue: String = EDIT_TEXT_DEF

    private var bottomHeight = 0
    private var baseGuidelineHeight = 0
    private var limitGuidelineHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val paramsGuideline = binding.topGuideline.layoutParams as ConstraintLayout.LayoutParams

            binding.topGuideline.layoutParams = upgradeGuideline(paramsGuideline, imeVisible,
                ime.bottom - systemBars.bottom,)

            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom
            )
            insets
        }

        initView()
        meteringSize()

        nameTextWatcher = initEditText(nameEditText, binding.nameTitle) { sString ->
            nameTextValue = sString
            activateCreateButton(sString.isNotEmpty())
        }
        descriptionTextWatcher =
            initEditText(descriptionEditText, binding.descriptionTitle) { sString ->
                descriptionTextValue = sString
            }

        viewModel.getScreenStateLiveData().observe(this) {

            if (savedInstanceState != null) {
                regenerationInstanceState(savedInstanceState)
            }
            showContentRender(it)
        }

        binding.createButton.setOnClickListener {

            playlist = Playlist(
                playlistName = nameTextValue,
                description = descriptionTextValue,
                fileUrl = fileUrlTextValue,
            )
            viewModel.savePlaylist(playlist)
        }

        binding.topToolbarFrame.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    viewModel.getImageAspectRatio(it)

                }
            }

        binding.buttonCropImage.setOnClickListener {

            val croppedBitmap = cropImageView!!.croppedImage
            if (croppedBitmap != null) {
                viewModel.saveBitmapToFile(croppedBitmap)
            }

        }

        binding.playlistImage.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.descriptionEditText.setOnFocusChangeListener { v, hasFocus ->
            bottomHeight = if (hasFocus) v.bottom else 0
        }

        binding.nameEditText.setOnFocusChangeListener { v, hasFocus ->
            bottomHeight = if (hasFocus) v.bottom else 0
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (deleteFlag)
                        activateBackDialog()
                    else {
                        this.remove()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    private fun upgradeGuideline(paramsGuideline: ConstraintLayout.LayoutParams,
                                 imeVisible: Boolean,
                                 imeBottomInset: Int): ViewGroup.LayoutParams? {
        if (imeVisible) {
            paramsGuideline.guidePercent = ScreenSize.getGuidePercentForNewPlaylistActivity(
                bottomHeight = bottomHeight,
                baseGuidelineHeight = baseGuidelineHeight,
                imeBottomInset = imeBottomInset,
                limitGuidelineHeight = limitGuidelineHeight)
        } else {
            paramsGuideline.guidePercent = 1f
        }
        return paramsGuideline
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("EDIT_NAME_TEXT_KEY", nameTextValue)
        outState.putString("EDIT_DESCRIPTION_TEXT_KEY", descriptionTextValue)
        outState.putBoolean("EDIT_NAME_FOCUS_KEY", nameEditText.hasFocus())
        outState.putBoolean("EDIT_DESCRIPTION_FOCUS_KEY", descriptionEditText.hasFocus())
    }

    private fun regenerationInstanceState(savedInstanceState: Bundle){
        nameTextValue = savedInstanceState.getString("EDIT_NAME_TEXT_KEY", EDIT_TEXT_DEF)
        descriptionTextValue = savedInstanceState.getString("EDIT_DESCRIPTION_TEXT_KEY", EDIT_TEXT_DEF)
        nameEditText.setText(nameTextValue)
        descriptionEditText.setText(descriptionTextValue)
        if(savedInstanceState.getBoolean("EDIT_NAME_FOCUS_KEY", false)) {nameEditText.requestFocus()}
        if(savedInstanceState.getBoolean("EDIT_DESCRIPTION_FOCUS_KEY", false)) {descriptionEditText.requestFocus()}

    }

    private fun initView() {
        nameEditText = binding.nameEditText
        descriptionEditText = binding.descriptionEditText

    }

    private fun initEditText(editText: EditText, textView: TextView, custOnTextChanged: (String) -> Unit = {},
    ): TextWatcher {

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                custOnTextChanged(s.toString())
                activateEnvirons(s.toString().isNotEmpty(), editText, textView)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        editText.addTextChangedListener(textWatcher)
        return textWatcher
    }

    private fun meteringSize(){

        binding.playlistImageFrameLayout.post {
            baseGuidelineHeight = binding.playlistImageFrameLayout.bottom
        }

        binding.topToolbarFrame.post {
            limitGuidelineHeight = binding.topToolbarFrame.bottom
        }
    }

    private fun activateEnvirons(activate : Boolean, editText: EditText, textView: TextView){
        editText.isSelected = activate
        setAnyContentFlag(activate)
        if(activate) textView.visibility = View.VISIBLE
        else textView.visibility = View.GONE
    }

    private fun activateCreateButton(activate : Boolean){
        binding.createButton.isEnabled = activate
    }

    private fun setAnyContentFlag(elementFlag : Boolean){

        deleteFlag = if (elementFlag) elementFlag else
            descriptionTextValue.isNotEmpty() || fileUrlTextValue.isNotEmpty() || nameTextValue.isNotEmpty()

    }

    private fun showContentRender(state: CreatePlaylistScreenState) {
        when (state) {
            is CreatePlaylistScreenState.InstallLogo -> showImage(state.url)
            is CreatePlaylistScreenState.ActivateCropper -> showCropper(state.uri)
            is CreatePlaylistScreenState.SavePlaylist -> showToast(state.newPlaylist.playlistName)
            is CreatePlaylistScreenState.ClosePlaylist -> closeActivity()
        }
    }

    private fun showToast(playlistName: String) {
        CustomToast(this, binding.root)
            .setMessage(getString(R.string.playlist_created, playlistName))
            .show()
        closeActivity()
    }

    private fun closeActivity() {
        finish()
    }

    private fun showCropper(uri: Uri) {

        cropImageView = binding.playlistCropImage
        cropImageView.setAspectRatio(1, 1)
        cropImageView.setFixedAspectRatio(true)
        cropImageView.setOnSetImageUriCompleteListener { view, uri, error ->
            if (error == null) {
                binding.dimOverlay.visibility = View.VISIBLE
                binding.buttonCropImage.visibility = View.VISIBLE

                binding.playlistImage.isClickable = false
            } else {
                binding.dimOverlay.visibility = View.GONE
                binding.buttonCropImage.visibility = View.GONE
            }
        }
        cropImageView.setImageUriAsync(uri)
    }

    private fun showImage(url :String){

        binding.playlistCropImage.visibility = View.GONE
        binding.buttonCropImage.visibility = View.GONE
        binding.dimOverlay.visibility = View.GONE
        binding.playlistImageFrameLayout.isSelected = true
        fileUrlTextValue = url
        setAnyContentFlag(true)
        Glide.with(this)
            .load(url)
            .error(R.drawable.vector_placeholder)
            .centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(SizeFormatter.dpToPx(8f, this)))
            .into(binding.playlistImage)
    }

    private fun activateBackDialog() {

        confirmDialog = MaterialAlertDialogBuilder(this,)
            .setTitle(this.getString(R.string.title_question_back_playlist))
            .setMessage(this.getString(R.string.description_question_back_playlist))
            .setNegativeButton(this.getString(R.string.finish_question_back_playlist)) { dialog, which ->
                negativeButtonAction()
            }
            .setNeutralButton(this.getString(R.string.comeback_question_back_playlist)) { dialog, which ->
            }
        confirmDialog.show()
    }

    private fun negativeButtonAction(){
        if(fileUrlTextValue.isNotEmpty()){
            viewModel.deleteFile(fileUrlTextValue)
        } else
            viewModel.finishActivity()
    }

    private fun getImagesFromStorage() {
        viewModel.getImagesFromStorage()
    }
}
