package com.silkysys.warehouse.app.picking.pick.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.silkysys.warehouse.R
import com.silkysys.warehouse.app.picking.pick.fragment.PickStartedFragment
import com.silkysys.warehouse.app.picking.pick.fragment.PickWaitingFragment

class PickPageAdapter(
    val context: AppCompatActivity,
    fm: FragmentManager,
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PickStartedFragment()
            }
            else -> {
                PickWaitingFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.pick_started)
            else -> {
                return context.getString(R.string.pick_waiting)
            }
        }
    }
}
