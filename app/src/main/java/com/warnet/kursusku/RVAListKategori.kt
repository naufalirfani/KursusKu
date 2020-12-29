package com.warnet.kursusku

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_kategori.view.*

class RVAListKategori(private val context: Context?, private val listKategori: ArrayList<String>, private val fotoKategori: ArrayList<Int>) : RecyclerView.Adapter<RVAListKategori.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_kategori, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.view.tv_item_kategori.text = listKategori[position]
        Glide.with(holder.itemView.context)
            .load(fotoKategori[position])
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
            .into(holder.view.iv_item_kategori)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", listKategori[position])
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listKategori.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}