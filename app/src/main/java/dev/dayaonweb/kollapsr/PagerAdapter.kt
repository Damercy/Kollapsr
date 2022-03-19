package dev.dayaonweb.kollapsr

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.dayaonweb.kollapsr.fragments.*

class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> CameraFragment()
            1 -> HomeFragment()
            2 -> SearchFragment()
            3 -> HistoryFragment()
            4 -> ProfileFragment()
            else -> HomeFragment()
        }

}