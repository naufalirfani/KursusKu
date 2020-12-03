package com.example.kwuapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invoice_bayar.*
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.actionbar
import java.text.SimpleDateFormat
import java.util.*

class InvoiceBayarActivity : AppCompatActivity() {

    private lateinit var userDetail: UserDetail
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private val bulan = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    private var  totalHarga: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_bayar)

        supportActionBar?.hide()

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.visibility = View.GONE
        val title = "Status Pembayaran"
        tvTitle.text = title
        tvTitle.gravity = Gravity.CENTER_HORIZONTAL

        totalHarga = intent.getStringExtra("totalHarga")


        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            invoicebayar_progressbar.visibility = View.VISIBLE
            loadUser()
        }

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
                    val pesan = "Pembayaran Berhasil"
                    tv_invoicebayar_selamat.text = pesan
                    tv_invoicebayar_isihpwa.text = userDetail.wa
                    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val dateArray = date.split("-")
                    val tahun = dateArray[0].toInt()
                    val bulan = dateArray[1].toInt() - 1
                    val tanggal = dateArray[2].toInt()
                    val hari = "$tanggal ${this.bulan[bulan]} $tahun"
                    tv_invoicebayar_isitanggal.text = hari
                    val metodeBayar = "Saldo KursusKu"
                    tv_invoicebayar_isimetodebayar.text = metodeBayar
                    tv_invoicebayar_isitotalharga.text = totalHarga

                    invoicebayar_progressbar.visibility = View.GONE
                }
                else{
                    loadUser()
                }
            }
            .addOnFailureListener { exception ->
                invoicebayar_progressbar.visibility = View.GONE
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }
}
