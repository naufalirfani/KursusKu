package com.warnet.kursusku

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var kursus: DataKursus
    private var arraySyarat = ArrayList<String>()
    private var arrayDipelajari = ArrayList<String>()
    private lateinit var userDetail: UserDetail
    private lateinit var dataUser: UserDetail
    private var userId: String = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var dbReference2: DatabaseReference
    private var user: FirebaseUser? = null
    private lateinit var adapter: RVAdapterDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.hide()

        kursus = intent.getParcelableExtra("kursus")!!
        tv_nama.text = kursus.nama

        dbReference2 = FirebaseDatabase.getInstance().getReference("keranjang")

        Glide.with(this).load(R.drawable.bouncy_balls).into(datail_progressBar)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        if (user != null) {
            userId = user!!.uid
            dbReference = FirebaseDatabase.getInstance().getReference("keranjang").child(userId).child(kursus.nama)
            tambahKeKeranjang(this)
            jumlahKeranjang(this)
        }
        else{
            btn_detail_addtokeranjang.setOnClickListener {
                val intent = Intent(this@DetailActivity, SignInActivity::class.java)
                startActivity(intent)
            }
            btn_detail_bayar.setOnClickListener {
                val intent = Intent(this@DetailActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }

        btn_detail_back.setOnClickListener {onBackPressed()}

        datail_progressBar.visibility = View.VISIBLE
        loadKursus()

        btn_detail_search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        btn_detail_keranjang.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }
        btn_detail_chat.setOnClickListener {
            Toast.makeText(this, "Masih dalam Tahap Pengembangan.", Toast.LENGTH_SHORT).show()
        }

        cv_addtochart.visibility = View.GONE
        tv_detail_jumlahkeranjang.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            tambahKeKeranjang(this)
            jumlahKeranjang(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(kursus.video != "kosong"){
            adapter.simpleExoPlayer.release()
        }

    }

    override fun onPause() {
        super.onPause()
        if(kursus.video != "kosong"){
            adapter.simpleExoPlayer.playWhenReady = false
            adapter.simpleExoPlayer.playbackState
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadKursus(){
        arraySyarat.clear()
        arrayDipelajari.clear()
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus").document(kursus.nama)
            .get()
            .addOnSuccessListener { result ->
                arraySyarat = result.get("syarat") as ArrayList<String>
                arrayDipelajari = result.get("dipelajari") as ArrayList<String>
                if(arraySyarat.isNotEmpty() && arrayDipelajari.isNotEmpty()){
                    rv_detail.setHasFixedSize(true)
                    rv_detail.layoutManager = LinearLayoutManager(this)
                    adapter = RVAdapterDetail(applicationContext, kursus, arrayDipelajari, arraySyarat)
                    adapter.notifyDataSetChanged()
                    rv_detail.adapter = adapter
                    rv_detail.scrollToPosition(0)

                    datail_progressBar.visibility = View.INVISIBLE
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                datail_progressBar.visibility = View.INVISIBLE
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
                    loadKursus()

                }
                snackBar.show()
            }
    }

    private fun tambahKeKeranjang(context: Context){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hasil = dataSnapshot.getValue(DataKeranjang::class.java)
                if(hasil == null){

                    tv_detail_jumlahkeranjang.visibility = View.GONE
                    btn_detail_addtokeranjang.setOnClickListener {
                        if (user != null) {
                            val harga = kursus.harga.replace(".", "").toLong()
                            val dataKeranjang = DataKeranjang(kursus.nama,1, harga)
                            dbReference2.child(userId).child(kursus.nama).setValue(dataKeranjang)
                            cv_addtochart.visibility = View.VISIBLE
                            val expandIn: Animation = AnimationUtils.loadAnimation(context, R.anim.expand_in)
                            cv_addtochart.startAnimation(expandIn)
                            val handler = Handler()
                            handler.postDelayed({ // Do something after 5s = 5000ms
                                cv_addtochart.visibility = View.GONE
                            }, 2000)
                        }
                        else{
                            val intent = Intent(this@DetailActivity, SignInActivity::class.java)
                            startActivity(intent)
                        }

                    }

                    btn_detail_bayar.setOnClickListener {
                        if (user != null) {
                            val harga = kursus.harga.replace(".", "").toLong()
                            val dataKeranjang = DataKeranjang(kursus.nama,1, harga)
                            dbReference2.child(userId).child(kursus.nama).setValue(dataKeranjang)
                            val intent = Intent(context, KeranjangActivity::class.java)
                            intent.putExtra("berasalDari", "DetailActivity")
                            intent.putExtra("kursusDibeli", kursus.nama)
                            startActivity(intent)
                        }
                        else{
                            val intent = Intent(this@DetailActivity, SignInActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                else{
                    btn_detail_addtokeranjang.setOnClickListener {
                        if (user != null) {
                            val harga = kursus.harga.replace(".", "").toLong() * hasil.jumlah!!.plus(1)
                            val dataKeranjang = DataKeranjang(kursus.nama, hasil.jumlah?.plus(1), harga)
                            dbReference2.child(userId).child(kursus.nama).setValue(dataKeranjang)
                            cv_addtochart.visibility = View.VISIBLE
                            val expandIn: Animation = AnimationUtils.loadAnimation(context, R.anim.expand_in)
                            cv_addtochart.startAnimation(expandIn)
                            val handler = Handler()
                            handler.postDelayed({ // Do something after 5s = 5000ms
                                cv_addtochart.visibility = View.GONE
                            }, 2500)
                        }
                        else{
                            val intent = Intent(this@DetailActivity, SignInActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    btn_detail_bayar.setOnClickListener {
                        if (user != null) {
                            val harga = kursus.harga.replace(".", "").toLong() * hasil.jumlah!!.plus(1)
                            val dataKeranjang = DataKeranjang(kursus.nama, hasil.jumlah?.plus(1), harga)
                            dbReference2.child(userId).child(kursus.nama).setValue(dataKeranjang)
                            val intent = Intent(context, KeranjangActivity::class.java)
                            intent.putExtra("berasalDari", "DetailActivity")
                            intent.putExtra("kursusDibeli", kursus.nama)
                            startActivity(intent)
                        }
                        else{
                            val intent = Intent(this@DetailActivity, SignInActivity::class.java)
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

    private fun jumlahKeranjang(context: Context){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var jumlah: Long = 0
                for(data in dataSnapshot.children){
                    val hasil = data.getValue(DataKeranjang::class.java)
                    if(hasil == null){
                        tv_detail_jumlahkeranjang.visibility = View.GONE
                    }
                    else{
                        tv_detail_jumlahkeranjang.visibility = View.VISIBLE
                        jumlah += hasil.jumlah!!
                    }
                }
                tv_detail_jumlahkeranjang.text = jumlah.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference2.child(userId).addValueEventListener(postListener)
    }
}
