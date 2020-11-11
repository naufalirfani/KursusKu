package com.example.kwuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_akun.*

@Suppress("DEPRECATION")
class AkunActivity : AppCompatActivity(){

    lateinit var userDetail: UserDetail
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun)

        supportActionBar?.hide()

        userDetail = intent.getParcelableExtra("userDetail")!!

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
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadUser()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        loadUser()
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
}