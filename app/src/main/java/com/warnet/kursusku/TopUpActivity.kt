package com.warnet.kursusku

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_keranjang.actionbar
import kotlinx.android.synthetic.main.activity_top_up.*


@Suppress("DEPRECATION")
class TopUpActivity : AppCompatActivity() {

    private var isTransferShow: Boolean = false
    private var isPulsaShow: Boolean = false
    private var isIndosatShow: Boolean = false
    private var isTselShow: Boolean = false
    private var bayarDipilih: String = ""
    private var userid: String? = null
    private lateinit var dbReference: DatabaseReference
    private var userDetail: UserDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up)

        supportActionBar?.hide()

        userid = intent.getStringExtra("userid")
        userDetail = intent.getParcelableExtra("akun")

        dbReference = FirebaseDatabase.getInstance().getReference("statusBayar")

        Glide.with(this).load(R.drawable.bouncy_balls).into(topup_progressBar)
        topup_progressBar.visibility = View.GONE

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.isi_saldo)
        actionbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"))
        btnBack.background = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)

        hideShow()

        btn_detail_bayar.setOnClickListener {
            if( bayarDipilih == "" && et_nominalsaldo.text.toString().isEmpty()){
                Toast.makeText(this, "Silahkan Masukkan Nominal Isi Saldo dan Pilih Metode Pembayaran", Toast.LENGTH_SHORT).show()
            }
            else if(et_nominalsaldo.text.toString() == ""){
                Toast.makeText(this, "Silahkan Masukkan Nominal Isi Saldo", Toast.LENGTH_SHORT).show()
            }
            else if(bayarDipilih == ""){
                Toast.makeText(this, "Silahkan Pilih Metode Pembayaran", Toast.LENGTH_SHORT).show()
            }
            else if(userDetail?.wa == "kosong"){
                Toast.makeText(this, "Harap Masukkan No. HP/WA", Toast.LENGTH_SHORT).show()
                topup_progressBar.visibility = View.VISIBLE
                val handler = Handler()
                handler.postDelayed({
                    val intent = Intent(this, SettingActivity::class.java)
                    intent.putExtra("userDetail", userDetail)
                    startActivity(intent)
                    topup_progressBar.visibility = View.GONE
                }, 3000)
            }
            else{
                val db2 = FirebaseFirestore.getInstance()
                db2.collection("statusBayar").document(userid!!)
                    .update("caraBayar", bayarDipilih)
                    .addOnSuccessListener { result2 ->
                    }
                    .addOnFailureListener { exception ->
                    }
                val jumlah = et_nominalsaldo.text.toString()
                db2.collection("statusBayar").document(userid!!)
                    .update("jumlah", jumlah.toLong())
                    .addOnSuccessListener { result2 ->
                    }
                    .addOnFailureListener { exception ->
                    }
                db2.collection("statusBayar").document(userid!!)
                    .update("status", "pending")
                    .addOnSuccessListener { result2 ->
                    }
                    .addOnFailureListener { exception ->
                    }

                val data = DataPesanan(bayarDipilih,0,jumlah.toLong(),"pending",0)
                dbReference.child(userid!!).setValue(data)

                val intent = Intent(applicationContext, InvoiceActivity::class.java)
                intent.putExtra("akun", userDetail)
                intent.putExtra("userid", userid)
                startActivity(intent)
            }
        }

        et_nominalsaldo.setOnClickListener { et_nominalsaldo.isCursorVisible = true }
        et_nominalsaldo.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    et_nominalsaldo.isCursorVisible = false
                    closeKeyBoard()
                    true
                }
                else -> false
            }
        }
    }

    private fun closeKeyBoard() {

        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

        }
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
            bayarDipilih = ""
            iv_pulsa_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            closeKeyBoard()
            et_nominalsaldo.isCursorVisible = false
            if(dialogTransfer.visibility == View.VISIBLE){
                iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
                animation.duration = 300
                dialogTransfer.animation = animation
                dialogTransfer.animate()
                animation.start()
                dialogTransfer.visibility = View.GONE
            }
            else{
                iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.down)
                animation.duration = 300
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
            tv_transfer_bri_carabayar.visibility = View.GONE
            tv_transfer_mandiri_carabayar.visibility = View.GONE
            rb_transfer_bri.isChecked = false
            dialog2.text = ""
            isTransferShow = false
            rb_transfer_mandiri.isChecked = false
            dialog3.text = ""
            isPulsaShow = false
            bayarDipilih = ""
            iv_trasnfer_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            closeKeyBoard()
            et_nominalsaldo.isCursorVisible = false
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
                bayarDipilih = ""
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
                bayarDipilih = "bri"
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
                bayarDipilih = ""
                isPulsaShow = !isPulsaShow
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
                animation.duration = 300
                dialog3.animation = animation
                dialog3.animate()
                animation.start()
                dialog3.visibility = View.GONE
            }
            else{
                bayarDipilih = "mandiri"
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
                bayarDipilih = ""
            }
            else{
                isIndosatShow = !isIndosatShow
                bayarDipilih = "indosat"
            }
        }

        rb_pulsa_tsel.setOnClickListener {
            rb_pulsa_tsel.isChecked = !isTselShow
            rb_pulsa_indosat.isChecked = false
            isIndosatShow = false
            if(isTselShow == true){
                isTselShow = !isTselShow
                bayarDipilih = ""
            }
            else{
                isTselShow = !isTselShow
                bayarDipilih = "tsel"
            }
        }
    }

}
