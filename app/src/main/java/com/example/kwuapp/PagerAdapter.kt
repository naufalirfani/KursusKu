package com.example.kwuapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@Suppress("DEPRECATION")
class PagerAdapter (fm: FragmentManager,
                    private val dataKursus: ArrayList<DataKursus>,
                    private val dataKursus2: ArrayList<DataKursus>,
                    private val dataKursus3: ArrayList<DataKursus>,
                    private val kategori: String,
                    private val kategori2: String) : FragmentStatePagerAdapter(fm){

    private val tabName : Array<String> = arrayOf("BERANDA", "KATEGORI")

    override fun getItem(position: Int): Fragment {
        return when (position) {

            0 -> BerandaFragment().newInstance(dataKursus)!!
            else -> KategoriFragment().newInstance(dataKursus2, dataKursus3, kategori, kategori2)!!
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? = tabName[position]
}