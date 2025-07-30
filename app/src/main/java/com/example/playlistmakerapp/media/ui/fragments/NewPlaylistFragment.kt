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
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentNewPlaylistBinding
import com.example.playlistmakerapp.media.domain.model.Playlist
import com.example.playlistmakerapp.media.ui.viewmodel.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var playlistName: String = ""
    private var playlistDescription: String? = null
    private var imageUri: Uri? = null


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
        setupTextWatchers()
        setupListeners()
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
            dialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            dialog()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
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
                val savedImagePath = imageUri?.let { uri ->
                    saveImageToInternalStorage(requireContext(), uri)
                }

                val newPlaylist = Playlist(
                    id = 0,
                    name = playlistName,
                    description = playlistDescription,
                    imagePath = savedImagePath, // Может быть null
                    trackIds = emptyList(),
                    tracksCount = 0
                )

                viewModel.savePlaylist(newPlaylist)
                Toast.makeText(
                    requireContext(),
                    "Плейлист \"$playlistName\" создан",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val directory = File(context.filesDir, "playlist_covers").apply { mkdirs() }
            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val outputFile = File(directory, fileName)

            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }


    private fun dialog() {
        if (imageUri != null || playlistName.isNotEmpty() || playlistDescription != null) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.alertTitle)
                .setMessage(R.string.alertDescr)
                .setNeutralButton(R.string.alertCancel) { dialog, which ->
                }
                .setPositiveButton(R.string.alertClose) { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()
        } else findNavController().navigateUp()
    }
}

