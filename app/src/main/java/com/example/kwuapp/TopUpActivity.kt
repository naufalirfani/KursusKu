package com.example.kwuapp

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_keranjang.actionbar
import kotlinx.android.synthetic.main.activity_top_up.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class TopUpActivity : AppCompatActivity() {

    private var isTransferShow: Boolean = false
    private var isPulsaShow: Boolean = false
    private var isIndosatShow: Boolean = false
    private var isTselShow: Boolean = false
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

        hideShow()
    }

    private fun hideShow(){
        val dialogTransfer: ConstraintLayout = findViewById(R.id.cons_transferbank)
        val dialogPulsa: ConstraintLayout = findViewById(R.id.cons_pulsadetail)
        val dialog2: TextView = findViewById(R.id.tv_transfer_bri_carabayar)
        val dialog3: TextView = findViewById(R.id.tv_transfer_mandiri_carabayar)

        cons_metodebayar.bringToFront()
        cons_transferbank.visibility = View.GONE
        iv_trasnfer_down.setOnClickListener {
            dialogPulsa.visibility = View.GONE
            rb_pulsa_tsel.isChecked = false
            isTselShow = false
            rb_pulsa_indosat.isChecked = false
            isIndosatShow = false
            iv_pulsa_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            if(dialogTransfer.visibility == View.VISIBLE){
                iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
                animation.duration = 500
                dialogTransfer.animation = animation
                dialogTransfer.animate()
                animation.start()
                dialogTransfer.visibility = View.GONE
            }
            else{
                iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.down)
                animation.duration = 500
                dialogTransfer.animation = animation
                dialogTransfer.animate()
                animation.start()
                dialogTransfer.visibility = View.VISIBLE
            }
        }

        cons_bayarpulsa.bringToFront()
        cons_pulsadetail.visibility = View.GONE
        iv_pulsa_down.setOnClickListener {
            dialogTransfer.visibility = View.GONE
            rb_transfer_bri.isChecked = false
            dialog2.text = ""
            isTransferShow = false
            rb_transfer_mandiri.isChecked = false
            dialog3.text = ""
            isPulsaShow = false
            iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            if(dialogPulsa.visibility == View.VISIBLE){
                iv_pulsa_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
                animation.duration = 300
                dialogPulsa.animation = animation
                dialogPulsa.animate()
                animation.start()
                dialogPulsa.visibility = View.GONE
            }
            else{
                iv_pulsa_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.down)
                animation.duration = 300
                dialogPulsa.animation = animation
                dialogPulsa.animate()
                animation.start()
                dialogPulsa.visibility = View.VISIBLE
            }
        }

        tv_transfer_bri_carabayar.visibility = View.GONE
        rb_transfer_bri.setOnClickListener{
            dialog3.visibility = View.GONE
            rb_transfer_bri.isChecked = !isTransferShow
            rb_transfer_mandiri.isChecked = false
            dialog3.text = ""
            isPulsaShow = false
            if(isTransferShow == true){
                dialog2.text = ""
                isTransferShow = !isTransferShow
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
                animation.duration = 300
                dialog2.animation = animation
                dialog2.animate()
                animation.start()
                dialog2.visibility = View.GONE
            }
            else{
                dialog2.text = resources.getString(R.string.carabayarbri)
                isTransferShow = !isTransferShow
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.down)
                animation.duration = 300
                dialog2.animation = animation
                dialog2.animate()
                animation.start()
                dialog2.visibility = View.VISIBLE
            }

        }

        tv_transfer_mandiri_carabayar.visibility = View.GONE
        rb_transfer_mandiri.setOnClickListener{
            dialog2.visibility = View.GONE
            rb_transfer_mandiri.isChecked = !isPulsaShow
            rb_transfer_bri.isChecked = false
            dialog2.text = ""
            isTransferShow = false
            if(isPulsaShow == true){
                dialog3.text = ""
                isPulsaShow = !isPulsaShow
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
                animation.duration = 300
                dialog3.animation = animation
                dialog3.animate()
                animation.start()
                dialog3.visibility = View.GONE
            }
            else{
                dialog3.text = resources.getString(R.string.carabayarmandiri)
                isPulsaShow = !isPulsaShow
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.down)
                animation.duration = 300
                dialog3.animation = animation
                dialog3.animate()
                animation.start()
                dialog3.visibility = View.VISIBLE
            }
        }

        rb_pulsa_indosat.setOnClickListener {
            rb_pulsa_indosat.isChecked = !isIndosatShow
            rb_pulsa_tsel.isChecked = false
            isTselShow = false
            if(isIndosatShow == true){
                isIndosatShow = !isIndosatShow
            }
            else{
                isIndosatShow = !isIndosatShow
            }
        }

        rb_pulsa_tsel.setOnClickListener {
            rb_pulsa_tsel.isChecked = !isTselShow
            rb_pulsa_indosat.isChecked = false
            isIndosatShow = false
            if(isTselShow == true){
                isTselShow = !isTselShow
            }
            else{
                isTselShow = !isTselShow
            }
        }
    }
}