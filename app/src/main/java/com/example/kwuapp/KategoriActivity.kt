package com.example.kwuapp

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_kategori.*

@Suppress("DEPRECATION")
class KategoriActivity : AppCompatActivity() {

    lateinit var kategori: String
    var arrayList: ArrayList<DataKursus> = arrayListOf()
    var dataKursus: ArrayList<DataKursus> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kategori)

        supportActionBar?.hide()
        kategori = intent.getStringExtra("kategori")!!
        tv_kategori3.text = kategori

        btn_back.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        val loading = ProgressDialog(this)
        loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.isIndeterminate = true
        loading.setCancelable(true)
        loading.show()
        loading.setContentView(R.layout.progressdialog)
        loadKursus(loading)
    }

    private fun loadKursus(loading2: ProgressDialog){
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                for (document in result) {
                    arrayList.add(DataKursus(document.getString("deskripsi")!!,
                        document.getString("dilihat")!!,
                        document.getString("gambar")!!,
                        document.getString("harga")!!,
                        document.getString("kategori")!!,
                        document.getString("nama")!!,
                        document.getString("pembuat")!!,
                        document.getString("pengguna")!!,
                        document.getString("rating")!!,
                        document.getString("remaining")!!))
                }

                if(arrayList.isNotEmpty()){
                    for(i in 0 until arrayList.size){
                        if(arrayList[i].kategori == kategori){
                            dataKursus.add(arrayList[i])
                        }
                    }
                    loading2.dismiss()
                    rv_katergori.setHasFixedSize(true)
                    rv_katergori.layoutManager = GridLayoutManager(this, 2)
                    val adapter = RVAdapterKursus(applicationContext, dataKursus)
                    adapter.notifyDataSetChanged()
                    rv_katergori.adapter = adapter
                }
                else{
                    loadKursus(loading2)
                }
            }
            .addOnFailureListener { exception ->
                loading2.dismiss()
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
                    loadKursus(loading2)

                }
                snackBar.show()
            }
    }
}
