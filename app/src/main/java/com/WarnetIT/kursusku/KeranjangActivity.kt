package com.WarnetIT.kursusku

import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.actionbar

@Suppress("DEPRECATION")
class KeranjangActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var arrayList = ArrayList<DataKursus>()
    private  var namaKursus: ArrayList<DataKeranjang> = arrayListOf()
    private var widthfix: Int = 0
    private var berasalDari: String? = null
    private var kursusDibeli: String? = null
    private lateinit var dbReference: DatabaseReference
    private lateinit var adapter: RVAKeranjangList
    private var userId: String = ""

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
            dbReference = FirebaseDatabase.getInstance().getReference("keranjang").child(userId)
            loadKeranjang()
            keranjang_progressbar.visibility = View.VISIBLE
        }
        else{
            val intent = Intent(this@KeranjangActivity, SignInActivity::class.java)
            startActivity(intent)
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

        adapter = RVAKeranjangList(this,
            widthfix,
            userId,
            tv_keranjang_totalharga,
            kursusDibeli,
            iv_keranjang_kosong,
            tv_keranjang_kosong,
            tv_keranjang_kosong2,
            btn_keranajng_bayar)
        adapter.notifyDataSetChanged()
        rv_keranjang.setHasFixedSize(true)
        rv_keranjang.layoutManager = LinearLayoutManager(this)
        rv_keranjang.adapter = adapter

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadKeranjang(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                namaKursus.clear()
                arrayList.clear()
                for( data in dataSnapshot.children){
                    val hasil = data.getValue(DataKeranjang::class.java)
                    if(hasil != null){
                        namaKursus.add(hasil)
                    }
                    else{
                        keranjang_progressbar.visibility = View.GONE
                        keranjang_cons_utama.setBackgroundColor(resources.getColor((R.color.white)))
                        iv_keranjang_kosong.visibility = View.VISIBLE
                        tv_keranjang_kosong.visibility = View.VISIBLE
                        tv_keranjang_kosong2.visibility = View.VISIBLE
                    }
                }
                if(namaKursus.isEmpty()){
                    keranjang_progressbar.visibility = View.GONE
                    keranjang_cons_utama.setBackgroundColor(resources.getColor((R.color.white)))
                    iv_keranjang_kosong.visibility = View.VISIBLE
                    tv_keranjang_kosong.visibility = View.VISIBLE
                    tv_keranjang_kosong2.visibility = View.VISIBLE
                }
                loadKursus(namaKursus)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                keranjang_progressbar.visibility = View.GONE
                val snackBar = Snackbar.make(
                    currentFocus!!, "    Connection Failure",
                    Snackbar.LENGTH_INDEFINITE
                )
                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(Color.BLACK)
                val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                textView.textSize = 16F
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning, 0, 0, 0)
                val snack_action_view = snackBarView.findViewById<Button>(R.id.snackbar_action)
                snack_action_view.setTextColor(Color.YELLOW)

                // Set an action for snack bar
                snackBar.setAction("Retry") {
                    loadKeranjang()

                }
                snackBar.show()
            }
        }
        dbReference.addValueEventListener(postListener)
    }

    private fun loadKursus(nama: ArrayList<DataKeranjang>){
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                for (document in result) {
                    for(i in 0 until nama.size){
                        if(document.getString("nama") == nama[i].namaKursus){
                            arrayList.add(DataKursus(document.getString("deskripsi")!!,
                                document.getLong("dilihat")!!,
                                document.getString("gambar")!!,
                                document.getString("harga")!!,
                                document.getString("kategori")!!,
                                document.getString("nama")!!,
                                document.getString("pembuat")!!,
                                document.getLong("pengguna")!!,
                                document.getString("rating")!!,
                                document.getString("remaining")!!))
                        }
                    }
                }

                if(arrayList.isNotEmpty()){
                    iv_keranjang_kosong.visibility = View.GONE
                    tv_keranjang_kosong.visibility = View.GONE
                    tv_keranjang_kosong2.visibility = View.GONE

                    adapter.setData(arrayList, namaKursus)

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
