package com.example.kwuapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout1: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        tabLayout1 = findViewById<View>(R.id.tabLayout) as TabLayout
        tabLayout1.addTab(tabLayout1.newTab().setText("BERANDA"))
        tabLayout1.addTab(tabLayout1.newTab().setText("KATEGORi"))
        tabLayout1.setTabTextColors(Color.parseColor("#BDBDBD"), Color.parseColor("#000000"))

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = pagerAdapter
        tabLayout1.setupWithViewPager(pager)
    }
}
