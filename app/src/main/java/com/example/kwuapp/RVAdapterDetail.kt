package com.example.kwuapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_detail.view.*

class RVAdapterDetail(private val context: Context?, private val listKursus: DataKursus) : RecyclerView.Adapter<RVAdapterDetail.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_detail, viewGroup, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val kursus = listKursus

        Glide.with(holder.itemView.context)
            .load(kursus.gambar)
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
            .into(holder.view.iv_detail)
        holder.view.tv_detail_nama.text = kursus.nama
        val harga = "Rp${kursus.harga}"
        holder.view.tv_detail_harga.text = harga
        holder.view.tv_detail_hargaasli.text = "Rp49.900"
        holder.view.tv_detail_hargaasli.setPaintFlags(holder.view.tv_detail_hargaasli.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        val ratingValue = kursus.rating.toFloat()
        holder.view.detail_ratingbar.rating = ratingValue
        holder.view.tv_detail_rating.text = kursus.rating
        val terjual = "${kursus.pengguna} Terjual"
        holder.view.tv_detail_terjual.text = terjual
        holder.view.tv_detail_deskripsi.text = kursus.deskripsi
    }

    override fun getItemCount(): Int {
        return 1
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}