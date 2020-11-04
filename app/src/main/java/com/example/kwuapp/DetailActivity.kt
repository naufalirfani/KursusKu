package com.example.kwuapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    lateinit var kursus: DataKursus
    var arraySyarat = ArrayList<String>()
    var arrayDipelajari = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.hide()

        kursus = intent.getParcelableExtra("kursus")!!
        tv_nama.text = kursus.nama

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
        btn_detail_addtokeranjang.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
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
}
