package com.example.playlistmakerapp.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmakerapp.databinding.FragmentSettingsBinding
import com.example.playlistmakerapp.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    private val settingsViewModel by viewModel<SettingsViewModel>() { parametersOf(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация свитчера текущим значением
        binding.switcher.isChecked = settingsViewModel.isDarkThemeEnabled()

        settingsViewModel.darkThemeEnabled.observe(viewLifecycleOwner) { isDark ->
            if (binding.switcher.isChecked != isDark) {
                binding.switcher.isChecked = isDark
            }
        }

        binding.switcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        binding.buttonShare.setOnClickListener {
            settingsViewModel.shareApp(requireContext())
        }

        binding.buttonSupport.setOnClickListener {
            settingsViewModel.openSupport(requireContext())
        }

        binding.buttonAgreement.setOnClickListener {
            settingsViewModel.openTerms(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}