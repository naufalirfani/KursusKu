package com.example.kwuapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@Suppress("DEPRECATION")
class PagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm){

    private val tabName : Array<String> = arrayOf("BERANDA", "KATEGORI")

    override fun getItem(position: Int): Fragment {
        return when (position) {

            0 -> BerandaFragment()
            else -> KategoriFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? = tabName[position]
}