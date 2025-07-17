package com.example.playlistmakerapp.media.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmakerapp.databinding.FragmentNewPlaylistBinding

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlistName: String = ""
    private var playlistDescription: String? = null


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
            findNavController().navigateUp()
        }

        binding.frameImage.setOnClickListener {

        }
    }
}

