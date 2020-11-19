package com.example.kwuapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.list_keranjang.view.*

class RVAKeranjangList(private val context: Context?,
                       private val dataKursus: ArrayList<DataKursus>,
                       private val width: Int,
                       private val isiKeranjang: String,
                       private val jumlahKeranjang: String,
                       private val userId: String,
                       private val tv_total: TextView) : RecyclerView.Adapter<RVAKeranjangList.Holder>() {

    private var isShow = false
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_keranjang, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val kursus = dataKursus[position]

        val params: ViewGroup.LayoutParams = holder.view.iv_item_keranjang.layoutParams
        params.width = width
        holder.view.iv_item_keranjang.layoutParams = params

        Glide.with(holder.view.context)
            .load(kursus.gambar)
//            .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
            .into(holder.view.iv_item_keranjang)
        holder.view.btn_item_keranjang_hapus.visibility = View.GONE
        holder.view.tv_item_keranjang_nama_utama.text = kursus.nama
        val harga = "Rp${kursus.harga}"
        holder.view.tv_item_keranjang_harga.text = harga

        holder.view.cb_item_keranjang.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(context, kursus.harga, Toast.LENGTH_SHORT).show()
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = 29000
                val totalHarga = hargaSebelumFix + hargaKursus
                if(totalHarga.toString().length > 3){
                    var x = totalHarga.toString()
                    x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
                    val textHarga = "Rp${x}"
                    tv_total.text = textHarga
                }
                else{
                    val textHarga = "Rp${totalHarga}"
                    tv_total.text = textHarga
                }
            }else{
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = 29000
                val totalHarga = hargaSebelumFix - hargaKursus
                if(totalHarga.toString().length > 3){
                    var x = totalHarga.toString()
                    x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
                    val textHarga = "Rp${x}"
                    tv_total.text = textHarga
                }
                else{
                    val textHarga = "Rp${totalHarga}"
                    tv_total.text = textHarga
                }
            }
        }

        holder.view.tv_item_keranjang_ubah.setOnClickListener {
            if(isShow){
                holder.view.tv_item_keranjang_ubah.text = holder.view.resources.getString(R.string.ubah)
                val dialog: TextView = holder.view.btn_item_keranjang_hapus
                val animation = AnimationUtils.loadAnimation(context, R.anim.right)
                animation.duration = 300
                dialog.animation = animation
                dialog.animate()
                animation.start()
                dialog.visibility = View.GONE
                isShow = false
            }
            else{
                holder.view.tv_item_keranjang_ubah.text = holder.view.resources.getString(R.string.selesai)
                val dialog: TextView = holder.view.btn_item_keranjang_hapus
                val animation = AnimationUtils.loadAnimation(context, R.anim.left)
                animation.duration = 300
                dialog.animation = animation
                dialog.animate()
                animation.start()
                dialog.visibility = View.VISIBLE
                isShow = true
            }
        }

        holder.view.btn_item_keranjang_hapus.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val kata = isiKeranjang.replace("$${kursus.nama}", "")
            db.collection("users2").document(userId)
                .update("isiKeranjang", kata)
                .addOnSuccessListener { result ->
                }
                .addOnFailureListener { exception ->
                }
            val jumlahKeranjangNew = jumlahKeranjang.toInt() - 1
            db.collection("users2").document(userId)
                .update("jumlahKeranjang", jumlahKeranjangNew.toString())
                .addOnSuccessListener { result ->
                }
                .addOnFailureListener { exception ->
                }

            dataKursus.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataKursus.size)
        }
    }

    override fun getItemCount(): Int {
        return dataKursus.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}