package com.example.playlistmakerapp.media.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentNewPlaylistBinding
import com.example.playlistmakerapp.media.ui.viewmodel.NewPlaylistState
import com.example.playlistmakerapp.media.ui.viewmodel.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var playlistId: Long = 0
    private var playlistName: String = ""
    private var playlistDescription: String? = null
    private var imageUri: Uri? = null
    private var initialImagePath: String? = null

    companion object {
        private const val PLAYLIST_ID_KEY = "playlist_id_key"

        fun newInstance(playlistId: Long = 0): NewPlaylistFragment {
            return NewPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putLong(PLAYLIST_ID_KEY, playlistId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getLong(PLAYLIST_ID_KEY, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initPlaylist(playlistId)
        setupObservers()
        setupTextWatchers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            binding.toolBar.title = state.title
            binding.createButton.text = state.buttonText

            when (state) {
                is NewPlaylistState.EditState -> {
                    binding.playlistNameEditText.setText(state.playlist.name)
                    binding.playlistDescriptionEditText.setText(state.playlist.description)
                    playlistName = state.playlist.name
                    playlistDescription = state.playlist.description
                    initialImagePath = state.playlist.imagePath

                    state.playlist.imagePath?.let { loadImage(it) }
                }

                is NewPlaylistState.CreateState -> {
                }
            }
        }
    }

    private fun loadImage(path: String) {
        Glide.with(binding.imagePlaylist)
            .load(File(path))
            .into(binding.imagePlaylist)
        binding.imagePlaylist.isVisible = true
        binding.icon.isVisible = false
    }

    private fun setupTextWatchers() {
        binding.playlistNameEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.createButton.isEnabled = false
            } else {
                binding.createButton.isEnabled = true
                playlistName = text.toString()
            }
        }

        binding.playlistDescriptionEditText.doOnTextChanged { text, _, _, _ ->
            playlistDescription = text?.toString()
        }
    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            checkForChangesAndNavigateBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            checkForChangesAndNavigateBack()
        }

        val pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                binding.imagePlaylist.setImageURI(uri)
                binding.imagePlaylist.isVisible = true
                binding.icon.isVisible = false
                imageUri = uri
            }
        }

        binding.frameImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            if (playlistName.isNotBlank()) {
                val savedImagePath = if (imageUri != null) {
                    saveImageToInternalStorage(requireContext(), imageUri!!)
                } else {
                    initialImagePath
                }

                viewModel.savePlaylist(
                    name = playlistName,
                    description = playlistDescription,
                    imagePath = savedImagePath
                )

                val message = if (playlistId != 0L) {
                    getString(R.string.playlist_updated, playlistName)
                } else {
                    getString(R.string.playlist_created, playlistName)
                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val directory = File(context.filesDir, "playlist_covers").apply { mkdirs() }
                val fileName = "cover_${System.currentTimeMillis()}.jpg"
                val outputFile = File(directory, fileName)

                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                outputFile.absolutePath
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun checkForChangesAndNavigateBack() {
        val hasChanges = when {
            imageUri != null -> true
            playlistId == 0L -> playlistName.isNotEmpty() || playlistDescription != null
            else -> {
                val currentName = binding.playlistNameEditText.text.toString()
                val currentDescription = binding.playlistDescriptionEditText.text?.toString()
                currentName != viewModel.getOriginalPlaylistName() ||
                        currentDescription != viewModel.getOriginalPlaylistDescription() ||
                        imageUri != null
            }
        }

        if (hasChanges) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_title)
            .setMessage(R.string.alert_description)
            .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.exit) { _, _ -> findNavController().navigateUp() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}