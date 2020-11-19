package com.example.kwuapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search.*


@Suppress("DEPRECATION")
class SearchActivity : AppCompatActivity() {

    var dataKursus = ArrayList<DataKursus>()
    val search = Search()
    private lateinit var dbReference: DatabaseReference
    private lateinit var etSearch: EditText
    private var listSearch: ArrayList<String> = arrayListOf()
    private var listSearch2: ArrayList<String> = arrayListOf()
    private var listSearchKategori = ArrayList<DataKursus>()
    private var listKosong: ArrayList<String> = arrayListOf()
    var kataSearch: String? = null
    var iterator: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        
        supportActionBar?.hide()
        tv_nothing.visibility = View.GONE
        tv_nothing2.visibility = View.GONE
        search_progressBar.visibility = View.GONE
        search_menu.visibility = View.GONE
        tv_search_history.visibility = View.VISIBLE
        btn_search_clear.visibility = View.VISIBLE
        btn_search_back.setOnClickListener { onBackPressed() }
        loadKursus()
        loadSearch()
        menuClick()

        search_btn.setOnClickListener { perfomSearch() }

        etSearch = findViewById(R.id.search_et)
        etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                perfomSearch()
                return@OnEditorActionListener true
            }
            false
        })


        btn_search_clear.setOnClickListener {
            listSearch.clear()
            dbReference = FirebaseDatabase.getInstance().getReference("search")
            val key: String? = dbReference.push().key
            val db = FirebaseFirestore.getInstance()
            val kata = "kosong"
            listKosong.add(kata)
            val city = DataSearch(kata, listKosong)
            db.collection("search").document("user1").set(city)
            search_rv2.visibility = View.GONE
            tv_nothing2.visibility = View.VISIBLE
            val searchkosong = "Tidak ada riwayat pencarian"
            tv_nothing2.text = searchkosong
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun perfomSearch(){
        startermenu()
        tv_search_history.visibility = View.GONE
        btn_search_clear.visibility = View.GONE
        search_rv2.visibility = View.GONE
        tv_nothing2.visibility = View.GONE
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
        val key: String? = dbReference.push().key
        val db = FirebaseFirestore.getInstance()
        val kata = etSearch.text.toString()
        listSearch.add(kata)
        val city = DataSearch(kata, listSearch)
        db.collection("search").document("user1").set(city)
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
                    search_progressBar.visibility = View.GONE
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

    fun loadSearch(){
        val db = FirebaseFirestore.getInstance()
        db.collection("search")
            .get()
            .addOnSuccessListener { result ->
                listSearch.clear()
                for (document in result) {
                    listSearch = document.get("listSearch") as ArrayList<String>
                    kataSearch = document.getString("kata")
                }

                if(listSearch.isNotEmpty()){
                    if(kataSearch != "kosong"){
                        listSearch2 = listSearch
                        listSearch2.remove("kosong")
                        search_progressBar.visibility = View.GONE
                        tv_nothing2.visibility = View.GONE
                        search_rv2.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)
                        val adapter = RVASearchHistory(this, listSearch2)
                        search_rv2.adapter = adapter
                    }
                    else{
                        search_progressBar.visibility = View.GONE
                        tv_nothing2.visibility = View.VISIBLE
                        val searchkosong = "Tidak ada riwayat pencarian"
                        tv_nothing2.text = searchkosong
                    }
                }
                else{
                    loadSearch()
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
                    loadSearch()

                }
                snackBar.show()
            }
    }
    fun startermenu(){
        cv_search_1.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
        tv_search_semua.setTextColor(resources.getColor(R.color.white))
        cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
        cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_desain.setTextColor(resources.getColor(R.color.black))
        cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_finansial.setTextColor(resources.getColor(R.color.black))
        cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
        cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_kantor.setTextColor(resources.getColor(R.color.black))
        cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
        cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
        tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))
    }

    fun menuClick(){
        cv_search_1.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_semua.setTextColor(resources.getColor(R.color.white))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

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
        }
        cv_search_2.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.white))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.bisnis)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
        cv_search_3.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_desain.setTextColor(resources.getColor(R.color.white))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.desain)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
        cv_search_4.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_finansial.setTextColor(resources.getColor(R.color.white))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.finasial)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
        cv_search_5.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.white))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.fotografi)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
        cv_search_6.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_kantor.setTextColor(resources.getColor(R.color.white))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.kantor)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
        cv_search_7.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.white))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.black))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.pendidikan)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
        cv_search_8.setOnClickListener {
            cv_search_1.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_semua.setTextColor(resources.getColor(R.color.black))
            cv_search_2.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_bisnis.setTextColor(resources.getColor(R.color.black))
            cv_search_3.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_desain.setTextColor(resources.getColor(R.color.black))
            cv_search_4.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_finansial.setTextColor(resources.getColor(R.color.black))
            cv_search_5.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_fotografi.setTextColor(resources.getColor(R.color.black))
            cv_search_6.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_kantor.setTextColor(resources.getColor(R.color.black))
            cv_search_7.setCardBackgroundColor(resources.getColor(R.color.white))
            tv_search_pendidikan.setTextColor(resources.getColor(R.color.black))
            cv_search_8.setCardBackgroundColor(resources.getColor(R.color.colorAbuGelap))
            tv_search_pengembangan.setTextColor(resources.getColor(R.color.white))

            listSearchKategori.clear()
            for(i in 0 until search.listSearch.size){
                if(search.listSearch[i].kategori == getString(R.string.pengembangan)){
                    listSearchKategori.add(search.listSearch[i])
                }
            }
            if(listSearchKategori.size == 0){
                tv_nothing.visibility = View.VISIBLE
                tv_nothing.text = getString(R.string.nothing_found)
            }
            else{
                tv_nothing.visibility = View.GONE
            }
            search_rv.setHasFixedSize(true)
            search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
            val adapter = RVAdapterKursus(applicationContext, listSearchKategori)
            adapter.notifyDataSetChanged()
            search_rv.adapter = adapter
        }
    }
}
