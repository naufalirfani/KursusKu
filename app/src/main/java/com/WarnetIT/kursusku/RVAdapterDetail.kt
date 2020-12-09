package com.WarnetIT.kursusku

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_detail.view.*

class RVAdapterDetail(private val context: Context?,
                      private val listKursus: DataKursus,
                      private val arrayDipelajari: ArrayList<String>,
                      private val arraySyarat: ArrayList<String>) : RecyclerView.Adapter<RVAdapterDetail.Holder>() {

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
        holder.view.tv_detail_hargaasli.paintFlags = holder.view.tv_detail_hargaasli.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        val ratingValue = kursus.rating.toFloat()
        holder.view.detail_ratingbar.rating = ratingValue
        holder.view.tv_detail_rating.text = kursus.rating
        val terjual = "${kursus.pengguna} Terjual"
        holder.view.tv_detail_terjual.text = terjual
        holder.view.tv_detail_deskripsi.text = kursus.deskripsi

        holder.view.rv_dipelajari.setHasFixedSize(true)
        holder.view.rv_dipelajari.layoutManager = LinearLayoutManager(context)
        val adapter = RVADetailList(arrayDipelajari, "dipelajari")
        adapter.notifyDataSetChanged()
        holder.view.rv_dipelajari.isNestedScrollingEnabled = false
        holder.view.rv_dipelajari.adapter = adapter

        holder.view.rv_syarat.setHasFixedSize(true)
        holder.view.rv_syarat.layoutManager = LinearLayoutManager(context)
        val adapter2 = RVADetailList(arraySyarat, "syarat")
        adapter2.notifyDataSetChanged()
        holder.view.rv_syarat.isNestedScrollingEnabled = false
        holder.view.rv_syarat.adapter = adapter2
    }

    override fun getItemCount(): Int {

        return 1
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}