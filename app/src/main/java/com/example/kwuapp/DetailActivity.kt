package com.example.kwuapp

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.list_kursus.*

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

                    loading2.dismiss()
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
