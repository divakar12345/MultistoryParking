package com.multistoryparking.app.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.multistoryparking.app.view.fragment.BikeAuditFragment
import com.multistoryparking.app.view.fragment.BusAuditFragment
import com.multistoryparking.app.view.fragment.CarAuditFragment

class ChildFragmentStateAdapter(private val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> BikeAuditFragment()
            1 -> CarAuditFragment()
            else -> BusAuditFragment()
        }
    }

}