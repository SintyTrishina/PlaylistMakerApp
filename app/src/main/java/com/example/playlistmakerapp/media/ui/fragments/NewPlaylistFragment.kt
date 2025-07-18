package com.example.playlistmakerapp.media.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.R
import com.example.playlistmakerapp.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

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
        val playlistNameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.createButton.isEnabled = false
                } else {
                    binding.createButton.isEnabled = true
                    playlistName = s.toString()
                }
            }
        }


        val playlistDescriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                playlistDescription = if (s.isNullOrEmpty()) {
                    null
                } else {
                    s.toString()
                }
            }
        }

        binding.playlistNameEditText.addTextChangedListener(playlistNameTextWatcher)
        binding.playlistDescriptionEditText.addTextChangedListener(playlistDescriptionTextWatcher)
    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            dialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dialog()
            }
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.imagePlaylist.setImageURI(uri)
                    binding.imagePlaylist.visibility = View.VISIBLE
                    binding.icon.visibility = View.GONE
                    imageUri = uri
                }
            }
        binding.frameImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            findNavController().navigateUp()
            Toast.makeText(requireContext(),"Плейлист $playlistName создан", Toast.LENGTH_LONG).show()
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

