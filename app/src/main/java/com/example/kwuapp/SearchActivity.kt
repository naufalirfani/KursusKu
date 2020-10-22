package com.example.kwuapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_search.*

@Suppress("DEPRECATION")
class SearchActivity : AppCompatActivity() {

    var dataKursus = ArrayList<DataKursus>()
    val search = Search()
    private lateinit var dbReference: DatabaseReference
    private lateinit var etSearch: EditText
    private var listSearch: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        
        supportActionBar?.hide()
        tv_nothing.visibility = View.GONE
        search_progressBar.visibility = View.GONE
        search_menu.visibility = View.GONE
        linear_search_history.visibility = View.VISIBLE
        btn_search_back.setOnClickListener { onBackPressed() }
        loadKursus()

        etSearch = findViewById(R.id.search_et)
        search_btn.setOnClickListener {
            linear_search_history.visibility = View.GONE
            search_progressBar.visibility = View.VISIBLE
            search_menu.visibility = View.VISIBLE
            search.listSearch.clear()
            search.searchJudul(etSearch.text.toString(), dataKursus)
            search_progressBar.visibility = View.GONE
            if(search.listSearch.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, search.listSearch)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter

            //close virtual keyboard
            closeKeyBoard()

            dbReference = FirebaseDatabase.getInstance().getReference("search")
            val key: String? = dbReference.push().getKey()
            val kata: String = etSearch.text.toString()
            val dataSearch = DataSearch(kata)
            dbReference.child(key.toString()).setValue(dataSearch)
        }

        getSearch()

        if(listSearch.isNotEmpty()){
            tv_nothing.visibility = View.GONE
        }
        else{
            tv_nothing.visibility = View.VISIBLE
            val searchkosong = "Tidak ada riwayat pencarian"
            tv_nothing.text = searchkosong
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.left, R.anim.right)
        finish()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun loadKursus(){
        search_progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                dataKursus.clear()
                for (document in result) {
                    dataKursus.add(DataKursus(document.getString("deskripsi")!!,
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

                if(dataKursus.isNotEmpty()){
                    getSearch()

                    if(listSearch.isNotEmpty()){
                        tv_nothing.visibility = View.GONE
                        search_rv.setHasFixedSize(true)
                        search_rv.layoutManager = LinearLayoutManager(applicationContext)
                        val adapter = RVASearchHistory(applicationContext, listSearch)
                        adapter.notifyDataSetChanged()
                        search_rv.adapter = adapter
                    }
                    else{
                        tv_nothing.visibility = View.VISIBLE
                        val searchkosong = "Tidak ada riwayat pencarian"
                        tv_nothing.text = searchkosong
                    }
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                search_progressBar.visibility = View.GONE
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

    fun getSearch(){
        val dbReference = FirebaseDatabase.getInstance().getReference("search")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(listSearch.isNotEmpty()){
                    listSearch.clear()
                }
                for (data: DataSnapshot in dataSnapshot.children){
                    val hasil = data.getValue(DataSearch::class.java)
                    listSearch.add(hasil?.kata!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference.addValueEventListener(postListener)
    }
}
