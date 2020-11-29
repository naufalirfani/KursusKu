package com.example.kwuapp

import android.app.AlertDialog
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_akun.*
import kotlin.random.Random


@Suppress("DEPRECATION")
class AkunActivity : AppCompatActivity(){

    lateinit var userDetail: UserDetail
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private lateinit var dbReference: DatabaseReference
    private var pesanan: Pesanan? = null

    private var arrayList = ArrayList<DataKursus>()
    private var arrayList2 = ArrayList<DataKursus>()
    private var arrayList3 = ArrayList<DataKursus>()
    private val kategori = arrayOf("Desain", "Bisnis", "Finansial", "Kantor", "Pendidikan", "Pengembangan")
    private var angka1: Int = 0
    private var angka2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun)

        supportActionBar?.hide()

        Glide.with(this).load(R.drawable.bouncy_balls).into(akun_progressbar)
        Glide.with(this).load(R.drawable.bouncy_balls).into(akun_progressbar_utama)
        akun_progressbar_utama.visibility = View.GONE
        akun_progressbar.bringToFront()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
        }

        btn_akun_back.setOnClickListener { loadKursus() }

        btn_akun_keranjang.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }

        btn_akun_keluar.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setMessage("Apakah Anda ingin keluar?")

            builder.setPositiveButton("Ya"
            ) { dialog, which -> // Do nothing but close the dialog
                akun_progressbar_utama.visibility = View.VISIBLE
                FirebaseAuth.getInstance().signOut()
                val handler = Handler()
                handler.postDelayed({
                    onBackPressed()
                    akun_progressbar_utama.visibility = View.GONE
                }, 3000)
                dialog.dismiss()
            }

            builder.setNegativeButton("Tidak"
            ) { dialog, which -> // Do nothing
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.setOnShowListener(OnShowListener {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.colorAbuGelap))
            })
            alert.show()
        }
        loadUser2()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            loadKursus()
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        akun_progressbar.visibility = View.GONE
        loadUser2()
    }

    private fun loadUser(){
        if(userDetail.gambar != "kosong"){
            Glide.with(applicationContext)
                .load(userDetail.gambar)
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL))
                .into(iv_akun_foto)
        }
        else{
            Glide.with(applicationContext)
                .load(resources.getDrawable(R.drawable.akun))
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL))
                .into(iv_akun_foto)
        }
        tv_akun_username.text = userDetail.username
        tv_akun_email.text = userDetail.email
        if(userDetail.saldo.length > 3){
            var x = userDetail.saldo
            x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
            val textHarga = "Rp $x"
            tv_akun_saldo.text = textHarga
        }
        else{
            val saldo = "Rp ${userDetail.saldo}"
            tv_akun_saldo.text = saldo
        }
    }

    private fun loadUser2() {
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
                    btn_akun_setting.setOnClickListener {
                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("userDetail", userDetail)
                        startActivity(intent)
                    }

                    btn_akun_setting2.setOnClickListener {
                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("userDetail", userDetail)
                        startActivity(intent)
                    }
                    iv_akun_foto.setOnClickListener {
                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("userDetail", userDetail)
                        startActivity(intent)
                    }
                    loadPesanan(userDetail, userId)

                    loadUser()
                }
                else{
                    loadUser2()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPesanan(user: UserDetail, id: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("statusBayar").document(userId)
            .get()
            .addOnSuccessListener { result ->
                pesanan = Pesanan(result.getString("caraBayar"),
                    result.getLong("durasi"),
                    result.getLong("jumlah"),
                    result.getString("status"),
                    result.getLong("waktu"))

                if(pesanan != null){
                    akun_progressbar.visibility = View.GONE
                    when (pesanan?.status) {
                        "batal" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, TopUpActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        "selesai" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, TopUpActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        "pending" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, InvoiceActivity::class.java)
                                intent.putExtra("akun", user)
                                intent.putExtra("userid", id)
                                startActivity(intent)
                            }
                        }
                    }
                }
                else{
                    loadPesanan(user, id)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadKursus(){
        akun_progressbar_utama.visibility = View.VISIBLE
        randomAngka()
        val kategori1 = kategori[angka1]
        val kategori2 = kategori[angka2]
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                arrayList2.clear()
                arrayList3.clear()
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
                    for(i in 0 until arrayList.size){
                        if(arrayList[i].kategori == kategori1){
                            arrayList2.add(arrayList[i])
                        }
                        if(arrayList[i].kategori == kategori2){
                            arrayList3.add(arrayList[i])
                        }
                    }

                    akun_progressbar_utama.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("arrayList", arrayList)
                    intent.putExtra("arrayList2", arrayList2)
                    intent.putExtra("arrayList3", arrayList3)
                    intent.putExtra("kategori1", kategori1)
                    intent.putExtra("kategori2", kategori2)
                    startActivity(intent)
                    finish()
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting documents: ", exception)
                Toast.makeText(this, "Koneksi error", Toast.LENGTH_SHORT).show()
            }
    }

    fun randomAngka(){
        angka1 = Random.nextInt(0,5)
        angka2 = Random.nextInt(0,5)
        if(angka1 == angka2){
            randomAngka()
        }
    }
}
