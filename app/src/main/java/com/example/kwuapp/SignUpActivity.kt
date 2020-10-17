package com.example.kwuapp

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    var isShow: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        iv_daftar_mata.setOnClickListener {
            if (isShow){
                et_daftar_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                iv_daftar_mata.background = getDrawable(R.drawable.matanutup)
                isShow = false
            }
            else{
                et_daftar_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                iv_daftar_mata.background = getDrawable(R.drawable.matabuka)
                isShow = true
            }
        }

        btn_daftar_back.setOnClickListener { onBackPressed() }

        tv_masuk.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
