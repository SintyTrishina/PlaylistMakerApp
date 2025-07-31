package com.example.playlistmakerapp.media.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmakerapp.media.ui.fragments.FavouriteFragment
import com.example.playlistmakerapp.media.ui.fragments.PlaylistsFragment

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavouriteFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}