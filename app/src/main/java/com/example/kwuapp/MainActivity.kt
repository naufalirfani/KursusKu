package com.example.kwuapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout1: TabLayout
    var arrayList = ArrayList<DataKursus>()
    var arrayList2 = ArrayList<DataKursus>()
    var arrayList3 = ArrayList<DataKursus>()
    val kategori = arrayOf("Desain", "Bisnis", "Finansial", "Kantor", "Pendidikan", "Pengembangan")
    var angka1: Int = 0
    var angka2: Int = 0

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        arrayList.clear()
        arrayList2.clear()
        arrayList3.clear()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val dpheight = displayMetrics.heightPixels
        val dpwidth = displayMetrics.widthPixels

        tabLayout1 = findViewById<View>(R.id.tabLayout) as TabLayout
        tabLayout1.addTab(tabLayout1.newTab().setText("BERANDA"))
        tabLayout1.addTab(tabLayout1.newTab().setText("KATEGORi"))
        tabLayout1.setTabTextColors(Color.parseColor("#BDBDBD"), Color.parseColor("#000000"))
        val params: ViewGroup.LayoutParams = tabLayout1.layoutParams
        params.width = dpwidth-350
        tabLayout1.layoutParams = params

        val btnSearch: Button = main_constraint.findViewById(R.id.btn_search)
        val btnAkun: Button = main_constraint.findViewById(R.id.btn_akun)
        val btnKeranjang: Button = main_constraint.findViewById(R.id.btn_keranjang)

        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        btnKeranjang.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }

        main_progressBar.visibility = View.VISIBLE
        loadKursus()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val name = intent.getStringExtra("username")
            val id = user.uid
            val email2 = user.email
            if(!TextUtils.isEmpty(name)){
                if(!(name!!.contains("@"))){
                    val db = FirebaseFirestore.getInstance()
                    val userDetail = UserDetail(name, email2.toString(), "kosong", "0")
                    db.collection("users2").document(id).set(userDetail)
                }
            }

            btnAkun.background = resources.getDrawable(R.drawable.akun)
            btnAkun.setOnClickListener {
                val intent2 = Intent(this, AkunActivity::class.java)
                startActivity(intent2)
            }
        }
        else{
            btnAkun.background = resources.getDrawable(R.drawable.login)
            btnAkun.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadKursus(){
        randomAngka()
        val kategori1 = kategori[angka1]
        val kategori2 = kategori[angka2]
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                arrayList2.clear()
                arrayList3.clear()
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
                        if(arrayList[i].kategori == kategori1){
                            arrayList2.add(arrayList[i])
                        }
                        if(arrayList[i].kategori == kategori2){
                            arrayList3.add(arrayList[i])
                        }
                    }
                    val pagerAdapter = PagerAdapter(supportFragmentManager, arrayList, arrayList2, arrayList3, kategori1, kategori2)
                    val pager = findViewById<View>(R.id.pager) as ViewPager
                    pager.adapter = pagerAdapter
                    tabLayout1.setupWithViewPager(pager)

                    main_progressBar.visibility = View.GONE
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                main_progressBar.visibility = View.GONE
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

    fun randomAngka(){
        angka1 = Random.nextInt(0,5)
        angka2 = Random.nextInt(0,5)
        if(angka1 == angka2){
            randomAngka()
        }
    }
}
