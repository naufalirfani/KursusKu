package com.example.kwuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_detail_list.view.*

class RVADetailList(private val list: ArrayList<String>) : RecyclerView.Adapter<RVADetailList.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_detail_list, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val list = list[position]
        holder.view.tv_list_dipelajari.text = list
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}