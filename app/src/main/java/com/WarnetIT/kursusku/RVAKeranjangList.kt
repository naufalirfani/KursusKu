package com.WarnetIT.kursusku

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_keranjang.view.*

@Suppress("DEPRECATION")
class RVAKeranjangList(private val context: Context?,
                       private val width: Int,
                       private val userId: String,
                       private val tv_total: TextView,
                       private var kursusDibeli: String?,
                       private var iv_kosong: ImageView,
                       private var tv_kosong: TextView,
                       private var tv_kosong2: TextView, private var btnBayar: Button) : RecyclerView.Adapter<RVAKeranjangList.Holder>() {

    private var isShow = false
    private val mData = ArrayList<DataKursus>()
    private val dataKursus = ArrayList<DataKeranjang>()
    private lateinit var dbReference: DatabaseReference
    private var isChecked: Boolean = false
    var kursusDipilih: ArrayList<String> = arrayListOf()
    private var textHarga: String = ""

    fun setData(items: ArrayList<DataKursus>, items2: ArrayList<DataKeranjang>) {
        mData.clear()
        mData.addAll(items)
        dataKursus.clear()
        dataKursus.addAll(items2)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_keranjang, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val kursus = mData[position]
        val keranjang = dataKursus[position]
        dbReference = FirebaseDatabase.getInstance().getReference("keranjang")

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
        holder.view.tv_item_keranjang_jumlah.text = keranjang.jumlah.toString()

        btnBayar()

        if (kursus.nama == kursusDibeli){
            isChecked = true
            holder.view.cb_item_keranjang.isChecked = true
            val hargaSebelum = tv_total.text.split("p")
            val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
            val hargaKursus = kursus.harga.replace(".","").toLong()
            val totalHarga = hargaSebelumFix + (hargaKursus * keranjang.jumlah!!)
            TambahTitikdiHarga(totalHarga, keranjang.jumlah!!)
            kursusDipilih.add(kursus.nama)
        }

        holder.view.iv_item_keranjang_add.setOnClickListener {
            val jumlah = holder.view.tv_item_keranjang_jumlah.text.toString()
            if(jumlah.toInt() > 0){
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = kursus.harga.replace(".","").toLong()
                val totalHarga = hargaSebelumFix + hargaKursus
                val dataKeranjang = DataKeranjang(kursus.nama, keranjang.jumlah?.plus(1), totalHarga)
                dbReference.child(userId).child(kursus.nama).setValue(dataKeranjang)
            }

            if (isChecked){
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = kursus.harga.replace(".","").toLong()
                val totalHarga = hargaSebelumFix + hargaKursus
                TambahTitikdiHarga(totalHarga, keranjang.jumlah!!)
            }
        }

        holder.view.iv_item_keranjang_remove.setOnClickListener {
            val jumlah = holder.view.tv_item_keranjang_jumlah.text.toString()
            if(jumlah.toInt() > 1){
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = kursus.harga.replace(".","").toLong()
                val totalHarga = hargaSebelumFix - hargaKursus
                val dataKeranjang = DataKeranjang(kursus.nama, keranjang.jumlah?.minus(1), totalHarga)
                dbReference.child(userId).child(kursus.nama).setValue(dataKeranjang)
            }

            if (isChecked){
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = kursus.harga.replace(".","").toLong()
                val totalHarga = hargaSebelumFix - hargaKursus
                TambahTitikdiHarga(totalHarga.toLong(), keranjang.jumlah!!)
            }
        }

        holder.view.cb_item_keranjang.setOnCheckedChangeListener { buttonView, isChecked ->
            inisiasiTotalharga(isChecked, keranjang.jumlah, kursus.nama, kursus.harga, keranjang.jumlah.toString(), kursus.harga)
            this.isChecked = isChecked
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
            isShow = false
            if (isChecked){
                val hargaSebelum = tv_total.text.split("p")
                val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
                val hargaKursus = kursus.harga.replace(".","").toLong()
                val totalHarga = hargaSebelumFix - (hargaKursus * keranjang.jumlah!!)
                TambahTitikdiHarga(totalHarga, keranjang.jumlah!!)
            }
            dbReference.child(userId).child(kursus.nama).removeValue()
        }

        if(mData.size == 0){
            iv_kosong.visibility = View.VISIBLE
            tv_kosong.visibility = View.VISIBLE
            tv_kosong2.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun inisiasiTotalharga(isChecked: Boolean, jumlah: Long?, namaKursus: String, hargaKursu: String, jumlahKursus:String, harga: String){
        if (isChecked){
            kursusDipilih.add(namaKursus)
            val hargaSebelum = tv_total.text.split("p")
            val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
            val hargaKursus = harga.replace(".","").toLong()
            val totalHarga = hargaSebelumFix + (hargaKursus * jumlah!!)
            TambahTitikdiHarga(totalHarga, jumlah)
        }else{
            kursusDipilih.remove(namaKursus)
            val hargaSebelum = tv_total.text.split("p")
            val hargaSebelumFix = hargaSebelum[1].replace(".", "").toInt()
            val hargaKursus = harga.replace(".","").toLong()
            val totalHarga = hargaSebelumFix - (hargaKursus * jumlah!!)
            TambahTitikdiHarga(totalHarga, jumlah)
        }
    }

    private fun TambahTitikdiHarga(totalHarga: Long, jumlah: Long?){
        if(totalHarga.toString().length in 3..6){
            var x = totalHarga.toString()
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            textHarga = "Rp${x}"
            tv_total.text = textHarga
        }
        else if(totalHarga.toString().length in 7..9){
            var x = totalHarga.toString()
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            x = x.substring(0, x.length-7) + "." + x.substring(x.length -7, x.length)
            textHarga = "Rp${x}"
            tv_total.text = textHarga
        }
        else if(totalHarga.toString().length in 10..12){
            var x = totalHarga.toString()
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            x = x.substring(0, x.length-7) + "." + x.substring(x.length -7, x.length)
            x = x.substring(0, x.length-11) + "." + x.substring(x.length -11, x.length)
            textHarga = "Rp${x}"
            tv_total.text = textHarga
        }
        else{
            textHarga = "Rp${totalHarga}"
            tv_total.text = textHarga
        }
    }

    private fun btnBayar(){
        btnBayar.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setMessage("Apakah Anda ingin Membayar dengan Saldo KursusKu?")

            builder.setPositiveButton("Ya"
            ) { dialog, which -> // Do nothing but close the dialog
                val intent = Intent(context, InvoiceBayarActivity::class.java)
                intent.putExtra("totalHarga", textHarga)
                intent.putExtra("kursusDipilih", kursusDipilih)
                context?.startActivity(intent)
            }

            builder.setNegativeButton("Tidak"
            ) { dialog, which -> // Do nothing
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.setOnShowListener {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(context?.resources!!.getColor(R.color.colorAbuGelap))
            }
            alert.show()
        }
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}