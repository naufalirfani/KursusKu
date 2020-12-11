package com.WarnetIT.kursusku

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_kategori.*

@Suppress("DEPRECATION")
class KategoriActivity : AppCompatActivity() {

    var kategori: String? = null
    var arrayList: ArrayList<DataKursus> = arrayListOf()
    var dataKursus: ArrayList<DataKursus> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kategori)

        supportActionBar?.hide()

        Glide.with(this).load(R.drawable.bouncy_balls).into(kategori_progressBar)

        kategori = intent.getStringExtra("kategori")
        tv_kategori3.text = kategori

        btn_back.setOnClickListener { onBackPressed() }

        loadKursus()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadKursus(){
        kategori_progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                for (document in result) {
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

                if(arrayList.isNotEmpty()){
                    for(i in 0 until arrayList.size){
                        if(arrayList[i].kategori == kategori){
                            dataKursus.add(arrayList[i])
                        }
                    }
                    kategori_progressBar.visibility = View.GONE
                    rv_katergori.setHasFixedSize(true)
                    rv_katergori.layoutManager = GridLayoutManager(this, 2)
                    val adapter = RVAdapterKursus(this, dataKursus)
                    adapter.notifyDataSetChanged()
                    rv_katergori.adapter = adapter
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                kategori_progressBar.visibility = View.GONE
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
