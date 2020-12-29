package com.warnet.kursusku

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.image_slider_layout_item.view.*
import kotlin.collections.ArrayList


class SliderAdapter(private val context: Context?, private var kursus: ArrayList<DataKursus>?) : SliderViewAdapter<SliderAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return Holder(inflate)
    }

    override fun onBindViewHolder(viewHolder: Holder, position: Int) {
        val sliderItem = kursus?.get(position)
        viewHolder.view.tv_auto_image_slider.text = sliderItem?.nama
        viewHolder.view.tv_auto_image_slider.textSize = 16f
        viewHolder.view.tv_auto_image_slider.setTextColor(Color.WHITE)
        Glide.with(viewHolder.itemView)
            .load(sliderItem?.gambar)
            .fitCenter()
            .into(viewHolder.view.iv_auto_image_slider)
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getCount(): Int {
        return 7
    }

    class Holder(val view: View) : SliderViewAdapter.ViewHolder(view)
}