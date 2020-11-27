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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.actionbar

@Suppress("DEPRECATION")
class KeranjangActivity : AppCompatActivity() {

    private lateinit var userDetail: UserDetail
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private var arrayList = ArrayList<DataKursus>()
    private  var namaKursus: ArrayList<String> = arrayListOf()
    private var widthfix: Int = 0
    private var berasalDari: String? = null
    private var kursusDibeli: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        supportActionBar?.hide()

        Glide.with(this).load(R.drawable.bouncy_balls).into(keranjang_progressbar)

        berasalDari = intent.getStringExtra("berasalDari")
        if (berasalDari == "DetailActivity"){
            kursusDibeli =intent.getStringExtra("kursusDibeli")
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dpheight = displayMetrics.heightPixels
        val dpwidth = displayMetrics.widthPixels
        widthfix = dpwidth/3

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            keranjang_progressbar.visibility = View.VISIBLE
            loadUser()
        }
        else{
            keranjang_progressbar.visibility = View.GONE
            keranjang_cons_utama.setBackgroundColor(resources.getColor((R.color.white)))
            iv_keranjang_kosong.visibility = View.VISIBLE
            tv_keranjang_kosong.visibility = View.VISIBLE
            tv_keranjang_kosong2.visibility = View.VISIBLE
        }

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.keranjang)
        actionbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"))
        btnBack.background = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)

        iv_keranjang_kosong.visibility = View.GONE
        tv_keranjang_kosong.visibility = View.GONE
        tv_keranjang_kosong2.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadUser() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users2").document(userId)
            .get()
            .addOnSuccessListener { result ->
                userDetail = UserDetail(result.getString("username").toString(),
                    result.getString("email").toString(),
                    result.getString("gambar").toString(),
                    result.getString("saldo").toString(),
                    result.getString("isiKeranjang").toString(),
                    result.getString("jumlahKeranjang").toString(),
                    result.getString("wa").toString())

                if(userDetail.isiKeranjang.isNotEmpty()){
                    if(userDetail.isiKeranjang != "kosong"){
                        val nama = userDetail.isiKeranjang.split("$")
                        for (i in nama.indices){
                            if(i > 0){
                                namaKursus.add(nama[i])
                            }
                        }
                        loadKursus(namaKursus)
                    }
                    else{
                        keranjang_progressbar.visibility = View.GONE
                        keranjang_cons_utama.setBackgroundColor(resources.getColor((R.color.white)))
                        iv_keranjang_kosong.visibility = View.VISIBLE
                        tv_keranjang_kosong.visibility = View.VISIBLE
                        tv_keranjang_kosong2.visibility = View.VISIBLE
                    }
                }
                else{
                    loadUser()
                }
            }
            .addOnFailureListener { exception ->
                keranjang_progressbar.visibility = View.GONE
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadKursus(nama: ArrayList<String>){
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                for (document in result) {
                    for(i in 0 until namaKursus.size){
                        if(document.getString("nama") == namaKursus[i]){
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
                    }
                }

                if(arrayList.isNotEmpty()){
                    iv_keranjang_kosong.visibility = View.GONE
                    tv_keranjang_kosong.visibility = View.GONE
                    tv_keranjang_kosong2.visibility = View.GONE

                    rv_keranjang.setHasFixedSize(true)
                    rv_keranjang.layoutManager = LinearLayoutManager(this)
                    val adapter = RVAKeranjangList(this,
                        arrayList,
                        widthfix,
                        userDetail.isiKeranjang,
                        userDetail.jumlahKeranjang,
                        userId,
                        tv_keranjang_totalharga,
                        kursusDibeli,
                        iv_keranjang_kosong,
                        tv_keranjang_kosong,
                        tv_keranjang_kosong2)
                    adapter.notifyDataSetChanged()
                    rv_keranjang.adapter = adapter

                    keranjang_progressbar.visibility = View.GONE
                }
                else{
                    loadKursus(namaKursus)
                }
            }
            .addOnFailureListener { exception ->
                keranjang_progressbar.visibility = View.GONE
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }
}
