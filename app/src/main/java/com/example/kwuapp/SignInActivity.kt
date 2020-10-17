package com.example.kwuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    var isShow: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.hide()

        iv_masuk_mata.setOnClickListener {
            if (isShow){
                et_masuk_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                iv_masuk_mata.background = getDrawable(R.drawable.matanutup)
                isShow = false
            }
            else{
                et_masuk_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                iv_masuk_mata.background = getDrawable(R.drawable.matabuka)
                isShow = true
            }
        }

        btn_masuk_back.setOnClickListener { onBackPressed() }

        tv_daftar.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
