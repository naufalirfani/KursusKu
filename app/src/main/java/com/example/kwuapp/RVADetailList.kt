package com.example.kwuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_detail_list.view.*

class RVADetailList(private val list: ArrayList<String>, private val jenisList: String) : RecyclerView.Adapter<RVADetailList.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_detail_list, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val list = list[position]
        holder.view.tv_list_dipelajari.text = list
        if (jenisList == "dipelajari"){
            Glide.with(holder.itemView.context)
                .load(holder.itemView.context.resources.getDrawable(R.drawable.ic_check_black_24dp))
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
                .into(holder.view.iv_detailList)
        }
        else if (jenisList == "syarat"){
            Glide.with(holder.itemView.context)
                .load(holder.itemView.context.resources.getDrawable(R.drawable.ic_check_circle_black_24dp))
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
                .into(holder.view.iv_detailList)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}