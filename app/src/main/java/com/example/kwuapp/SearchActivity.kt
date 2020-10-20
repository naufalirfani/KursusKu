package com.example.kwuapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_search.*

@Suppress("DEPRECATION")
class SearchActivity : AppCompatActivity() {

    var dataKursus = ArrayList<DataKursus>()
    val search = Search()
    private lateinit var etSearch: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        
        supportActionBar?.hide()
        tv_nothing.visibility = View.GONE
        search_progressBar.visibility = View.GONE
        btn_search_back.setOnClickListener { onBackPressed() }
        dataKursus = intent.getParcelableArrayListExtra<DataKursus>("dataKursus")!!

        search_rv.setHasFixedSize(true)
        search_rv.layoutManager = GridLayoutManager(applicationContext, 2)
        val adapter = RVAdapterKursus(applicationContext, dataKursus)
        adapter.notifyDataSetChanged()
        search_rv.adapter = adapter

        etSearch = findViewById(R.id.search_et)
        search_btn.setOnClickListener {
            search_progressBar.visibility = View.VISIBLE
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
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
