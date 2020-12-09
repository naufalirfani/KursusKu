package com.WarnetIT.kursusku

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_kodekursus.view.*

class RVAInvoiceBayar(private val context: Context?,
                      private val listnama: ArrayList<String>,
                      private val listharga: ArrayList<String>,
                      private val listjumlah: ArrayList<String>) : RecyclerView.Adapter<RVAInvoiceBayar.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_kodekursus, viewGroup, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val namaKursus = listnama[position]
        var hargakursus = listharga[position]
        val jumlahkursus = listjumlah[position]
        holder.view.tv_invoicebayar_list_namakursus.text = namaKursus
        hargakursus = hargakursus.replace(".", "")
        val harga2 = TambahTitikdiHarga(hargakursus.toLong())
        holder.view.tv_invoicebayar_list_jumlah.text = "x$jumlahkursus"
        holder.view.tv_invoicebayar_list_harga.text = harga2
        for (i in 0 until jumlahkursus.toInt()){
            if(i == 0){
                holder.view.tv_invoicebayar_list_kodekursus.text = getKodePromo()
            }
            else{
                holder.view.tv_invoicebayar_list_kodekursus.append("\n")
                holder.view.tv_invoicebayar_list_kodekursus.append(getKodePromo())
            }
        }
    }

    override fun getItemCount(): Int {
        return listnama.size
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    fun getKodePromo(): String{
        val list: MutableList<Any> = mutableListOf()
        for(i in 0 until 15){
            val angka1 = (1..2).random()
            if(angka1 == 1){
                val random = (0..9).random()
                list.add(random)
            }
            else{
                val random = ('A'..'Z').random()
                list.add(random)
            }
        }
        val hasil = list.joinToString(separator = "")
        return hasil
    }

    private fun TambahTitikdiHarga(totalHarga: Long): String{
        if(totalHarga.toString().length in 3..6){
            var x = totalHarga.toString()
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            val textHarga = "Rp${x}"
            return textHarga
        }
        else if(totalHarga.toString().length in 7..9){
            var x = totalHarga.toString()
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            x = x.substring(0, x.length-7) + "." + x.substring(x.length -7, x.length)
            val textHarga = "Rp${x}"
            return textHarga
        }
        else if(totalHarga.toString().length in 10..12){
            var x = totalHarga.toString()
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            x = x.substring(0, x.length-7) + "." + x.substring(x.length -7, x.length)
            x = x.substring(0, x.length-11) + "." + x.substring(x.length -11, x.length)
            val textHarga = "Rp${x}"
            return textHarga
        }
        else{
            val textHarga = "Rp${totalHarga}"
            return textHarga
        }
    }
}