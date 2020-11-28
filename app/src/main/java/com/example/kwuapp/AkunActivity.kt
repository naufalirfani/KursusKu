package com.example.kwuapp

import android.app.AlertDialog
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_akun.*


@Suppress("DEPRECATION")
class AkunActivity : AppCompatActivity(){

    lateinit var userDetail: UserDetail
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private lateinit var dbReference: DatabaseReference
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

        btn_akun_back.setOnClickListener { onBackPressed() }

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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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
        val saldo = "Rp ${userDetail.saldo}"
        tv_akun_saldo.text = saldo
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
                    akun_progressbar.visibility = View.GONE
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
                    btn_akun_isisaldo.setOnClickListener {
                        akun_progressbar_utama.visibility = View.VISIBLE
                        dbReference = FirebaseDatabase.getInstance().getReference("statusBayar")
                        val postListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (data: DataSnapshot in dataSnapshot.children){
                                    akun_progressbar_utama.visibility = View.GONE
                                    val hasil = data.getValue(Pesanan::class.java)
                                    when (hasil?.status) {
                                        "batal" -> {
                                            val intent = Intent(applicationContext, TopUpActivity::class.java)
                                            startActivity(intent)
                                        }
                                        "selesai" -> {
                                            val intent = Intent(applicationContext, TopUpActivity::class.java)
                                            startActivity(intent)
                                        }
                                        "pending" -> {
                                            val intent = Intent(applicationContext, InvoiceActivity::class.java)
                                            intent.putExtra("akun", userDetail)
                                            intent.putExtra("userid", userId)
                                            startActivity(intent)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        }
                        dbReference.addValueEventListener(postListener)
                    }
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
}
