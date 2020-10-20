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
    var tempat: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.hide()

        kursus = intent.getParcelableExtra("kursus")!!
        tempat = intent.getStringExtra("tempat")
        tv_nama.text = kursus.nama

        btn_detail_back.setOnClickListener {onBackPressed()}
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val tempat2 = tempat!!.split(" ")
        if(tempat == "main"){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if(tempat2[0] == "kategori"){
            val intent = Intent(this, KategoriActivity::class.java)
            intent.putExtra("kategori", tempat2[1])
            startActivity(intent)
            finish()
        }
        else if(tempat == "search"){
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        datail_progressBar.visibility = View.VISIBLE
        loadKursus()
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
                    Toast.makeText(this, "ada isinya broo ${arraySyarat[0]}", Toast.LENGTH_LONG).show()
                    rv_detail.setHasFixedSize(true)
                    rv_detail.layoutManager = LinearLayoutManager(this)
                    val adapter = RVAdapterDetail(applicationContext, kursus)
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
