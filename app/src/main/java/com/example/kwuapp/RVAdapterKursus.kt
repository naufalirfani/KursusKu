package com.example.kwuapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_kursus.view.*

class RVAdapterKursus(private val context: Context?, private val listKursus: ArrayList<DataKursus>, private var tempat: String) : RecyclerView.Adapter<RVAdapterKursus.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_kursus, viewGroup, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val kursus = listKursus[position]

        Glide.with(holder.itemView.context)
            .load(kursus.gambar)
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
            .into(holder.view.img_item_photo)

        holder.view.tv_item_remain.text = kursus.remaining
        holder.view.tv_item_nama.text = kursus.nama
        holder.view.tv_item_pembuat.text = kursus.pembuat
        holder.view.tv_item_rating.text = kursus.rating
        holder.view.tv_jumlah_rating.text = "(0)"
        holder.view.tv_item_harga.text = "Rp${kursus.harga}"
        holder.view.tv_hargaasli.text = "Rp49.900"
        holder.view.tv_hargaasli.setPaintFlags(holder.view.tv_hargaasli.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        holder.view.tv_item_pembeli.text = kursus.pengguna
        holder.view.tv_item_dilihat.text = kursus.dilihat

        val ratingValue = kursus.rating.toFloat()
        holder.view.ratingbar.rating = ratingValue

        val data = DataKursus(
            kursus.deskripsi,
            kursus.dilihat,
            kursus.gambar,
            kursus.harga,
            kursus.kategori,
            kursus.nama,
            kursus.pembuat,
            kursus.pengguna,
            kursus.rating,
            kursus.remaining
        )

        holder.view.cvList.setOnClickListener{
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("kursus", data)
            intent.putExtra("tempat", tempat)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listKursus.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}