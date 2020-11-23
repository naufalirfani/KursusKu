package com.example.kwuapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.hide()

        kursus = intent.getParcelableExtra("kursus")!!
        tv_nama.text = kursus.nama

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            loadUser()
        }
        else{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
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

        cv_addtochart.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            loadUser()
        }
        else{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
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
                    val adapter = RVAdapterDetail(applicationContext, kursus, arrayDipelajari, arraySyarat)
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
                Log.d("Error", "Error getting documents: ", exception)
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

                    btn_detail_addtokeranjang.setOnClickListener {
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("users2").document(userId)
                            .update("isiKeranjang", userDetail.isiKeranjang + "$${kursus.nama}")
                            .addOnSuccessListener { result ->
                                loadUser()
                            }
                            .addOnFailureListener { exception ->
                            }
                        val jumlahKeranjang = userDetail.jumlahKeranjang.toInt() + 1
                        db2.collection("users2").document(userId)
                            .update("jumlahKeranjang", jumlahKeranjang.toString())
                            .addOnSuccessListener { result ->
                                loadUser()
                            }
                            .addOnFailureListener { exception ->
                            }

                        cv_addtochart.visibility = View.VISIBLE
                        val expandIn: Animation = AnimationUtils.loadAnimation(this, R.anim.expand_in)
                        cv_addtochart.startAnimation(expandIn)
                        val handler = Handler()
                        handler.postDelayed({ // Do something after 5s = 5000ms
                            cv_addtochart.visibility = View.GONE
                        }, 2500)

                    }

                    btn_detail_bayar.setOnClickListener {
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("users2").document(userId)
                            .update("isiKeranjang", userDetail.isiKeranjang + "$${kursus.nama}")
                            .addOnSuccessListener { result ->
                            }
                            .addOnFailureListener { exception ->
                            }

                        val jumlahKeranjang = userDetail.jumlahKeranjang.toInt() + 1
                        db2.collection("users2").document(userId)
                            .update("jumlahKeranjang", jumlahKeranjang.toString())
                            .addOnSuccessListener { result ->
                            }
                            .addOnFailureListener { exception ->
                            }

                        val intent = Intent(this, KeranjangActivity::class.java)
                        intent.putExtra("berasalDari", "DetailActivity")
                        intent.putExtra("kursusDibeli", kursus.nama)
                        startActivity(intent)

                    }
                    datail_progressBar.visibility = View.GONE
                }
                else{
                    loadUser()
                }
            }
            .addOnFailureListener { exception ->
                datail_progressBar.visibility = View.GONE
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }
}
