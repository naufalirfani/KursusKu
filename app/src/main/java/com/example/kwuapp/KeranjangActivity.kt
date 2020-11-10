package com.example.kwuapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.actionbar

@Suppress("DEPRECATION")
class KeranjangActivity : AppCompatActivity() {

    var userDetail: UserDetail? = null
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private var arrayList = ArrayList<DataKursus>()
    private  var namaKursus: ArrayList<String> = arrayListOf()
    private var widthfix: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        supportActionBar?.hide()

        userDetail = intent.getParcelableExtra("userDetail")

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dpheight = displayMetrics.heightPixels
        val dpwidth = displayMetrics.widthPixels
        widthfix = dpwidth/3

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
        }
        else{
            iv_keranjang_kosong.visibility = View.VISIBLE
            tv_keranjang_kosong.visibility = View.VISIBLE
            tv_keranjang_kosong2.visibility = View.VISIBLE
            rv_keranjang.visibility = View.GONE
        }

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.keranjang)
        actionbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"))
        btnBack.background = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)

        if(userDetail?.isiKeranjang == "kososng"){
            iv_keranjang_kosong.visibility = View.GONE
            tv_keranjang_kosong.visibility = View.GONE
            tv_keranjang_kosong2.visibility = View.GONE
            rv_keranjang.visibility = View.GONE
        }

        keranajng_progressbar.visibility = View.VISIBLE
        if(userDetail?.username != "kosong"){
            val nama = userDetail?.isiKeranjang?.split("$")
            for (i in nama!!.indices){
                if(i > 0){
                    namaKursus.add(nama[i])
                }
            }
            loadKursus(namaKursus)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadKursus(nama: ArrayList<String>){
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                for (document in result) {
                    arrayList.add(DataKursus(document.getString("deskripsi")!!,
                        document.getString("dilihat")!!,
                        document.getString("gambar")!!,
                        document.getString("harga")!!,
                        document.getString("kategori")!!,
                        document.getString("nama")!!,
                        document.getString("pembuat")!!,
                        document.getString("pengguna")!!,
                        document.getString("rating")!!,
                        document.getString("remaining")!!))
                }

                if(arrayList.isNotEmpty()){
                    rv_keranjang.visibility = View.VISIBLE
                    iv_keranjang_kosong.visibility = View.GONE
                    tv_keranjang_kosong.visibility = View.GONE
                    tv_keranjang_kosong2.visibility = View.GONE

                    rv_keranjang.setHasFixedSize(true)
                    rv_keranjang.layoutManager = LinearLayoutManager(this)
                    val adapter = RVAKeranjangList(this, arrayList, widthfix)
                    adapter.notifyDataSetChanged()
                    rv_keranjang.adapter = adapter

                    keranajng_progressbar.visibility = View.GONE
                }
                else{
                    loadKursus(namaKursus)
                }
            }
            .addOnFailureListener { exception ->
                keranajng_progressbar.visibility = View.GONE
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }
}
