package com.example.kwuapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.actionbar
import kotlinx.android.synthetic.main.activity_top_up.*

@Suppress("DEPRECATION")
class TopUpActivity : AppCompatActivity() {

    private var isTransferShow: Boolean = false
    private var isPulsaShow: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up)

        supportActionBar?.hide()

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.isi_saldo)
        actionbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"))
        btnBack.background = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)

        cons_metodebayar.bringToFront()
        cons_transferbank.visibility = View.GONE
        iv_trasnfer_down.setOnClickListener {
            if(isTransferShow){
                iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                val animHide: Animation = AnimationUtils.loadAnimation(this, R.anim.up)
                cons_transferbank.startAnimation(animHide)
                cons_transferbank.visibility = View.GONE
                isTransferShow = false
            }
            else{
                iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                val animShow: Animation = AnimationUtils.loadAnimation(this, R.anim.down)
                cons_transferbank.startAnimation(animShow)
                cons_transferbank.visibility = View.VISIBLE
                isTransferShow = true
            }
        }

        cons_bayarpulsa.bringToFront()
        cons_pulsadetail.visibility = View.GONE
        iv_pulsa_down.setOnClickListener {
            if(isPulsaShow){
                iv_pulsa_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                val animHide: Animation = AnimationUtils.loadAnimation(this, R.anim.up)
                cons_pulsadetail.startAnimation(animHide)
                cons_pulsadetail.visibility = View.GONE
                isPulsaShow = false
            }
            else{
                iv_pulsa_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                val animShow: Animation = AnimationUtils.loadAnimation(this, R.anim.down)
                cons_pulsadetail.startAnimation(animShow)
                cons_pulsadetail.visibility = View.VISIBLE
                isPulsaShow = true
            }
        }
    }
}
