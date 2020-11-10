package com.example.kwuapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.list_history.view.*
import kotlinx.android.synthetic.main.list_keranjang.view.*

class RVAKeranjangList(private val context: Context?, private val userDetail: ArrayList<DataKursus>, private val width: Int) : RecyclerView.Adapter<RVAKeranjangList.Holder>() {

    private var isShow = true
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_keranjang, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val kursus = userDetail[position]

        val params: ViewGroup.LayoutParams = holder.view.iv_item_keranjang.layoutParams
        params.width = width
        holder.view.iv_item_keranjang.layoutParams = params

        val params2: ViewGroup.LayoutParams = holder.view.tv_item_keranjang_nama.layoutParams
        params2.width = width
        holder.view.tv_item_keranjang_nama.layoutParams = params2

        Glide.with(holder.view.context)
            .load(kursus.gambar)
            .apply(
                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                    Target.SIZE_ORIGINAL))
            .into(holder.view.iv_item_keranjang)
        holder.view.btn_item_keranjang_hapus.visibility = View.GONE
        holder.view.tv_item_keranjang_nama.text = kursus.nama
        holder.view.tv_item_keranjang_harga.text = kursus.harga
        if(isShow){
            holder.view.tv_item_keranjang_ubah.setOnClickListener {
                holder.view.btn_item_keranjang_hapus.visibility = View.GONE
                isShow = false
            }
        }
        else{
            holder.view.tv_item_keranjang_ubah.setOnClickListener {
                holder.view.btn_item_keranjang_hapus.visibility = View.VISIBLE
            }
            isShow = true
        }

    }

    override fun getItemCount(): Int {
        return userDetail.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}