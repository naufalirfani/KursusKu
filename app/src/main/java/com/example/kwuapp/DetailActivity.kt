package com.example.kwuapp

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.list_kursus.*

class DetailActivity : AppCompatActivity() {

    lateinit var kursus: DataKursus
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.hide()

        kursus = intent.getParcelableExtra("kursus")!!
        tv_nama.text = kursus.nama

        rv_detail.setHasFixedSize(true)
        rv_detail.layoutManager = LinearLayoutManager(this)
        val adapter = RVAdapterDetail(applicationContext, kursus)
        adapter.notifyDataSetChanged()
        rv_detail.adapter = adapter
    }
}
