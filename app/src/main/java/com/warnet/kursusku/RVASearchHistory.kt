package com.warnet.kursusku

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_history.view.*

class RVASearchHistory(private val context: Context?, private val listSearch: ArrayList<String>) : RecyclerView.Adapter<RVASearchHistory.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_history, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val search = listSearch[position]
        holder.view.tv_history.text = search
    }

    override fun getItemCount(): Int {
        return listSearch.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}