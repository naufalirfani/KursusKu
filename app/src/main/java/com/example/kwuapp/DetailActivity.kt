package com.example.kwuapp

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        inisiasi()
    }

    fun inisiasi(){
        Glide.with(this)
            .load(kursus.gambar)
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
            .into(iv_detail)
        tv_detail_nama.text = kursus.nama
        val harga = "Rp${kursus.harga}"
        tv_detail_harga.text = harga
        tv_detail_hargaasli.text = "Rp49.900"
        tv_detail_hargaasli.setPaintFlags(tv_detail_hargaasli.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        val ratingValue = kursus.rating.toFloat()
        detail_ratingbar.rating = ratingValue
        tv_detail_rating.text = kursus.rating
        val terjual = "${kursus.pengguna} Terjual"
        tv_detail_terjual.text = terjual
    }
}
