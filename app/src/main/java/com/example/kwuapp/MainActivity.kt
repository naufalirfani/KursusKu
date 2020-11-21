package com.example.kwuapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout1: TabLayout
    private var arrayList = ArrayList<DataKursus>()
    private var arrayList2 = ArrayList<DataKursus>()
    private var arrayList3 = ArrayList<DataKursus>()
    private var angka1: Int = 0
    private var angka2: Int = 0
    private lateinit var kategori1: String
    private lateinit var kategori2: String

    private lateinit var userDetail: UserDetail
    private lateinit var dataUser: UserDetail
    private var userId: String = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var btnAkun: ImageView
    private lateinit var btnKeranjang: Button
    private lateinit var btnSearch: Button
    private var doubleBackToExitPressedOnce = false
    private val TIME_DELAY = 2000
    private val back_pressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        arrayList.clear()
        arrayList2.clear()
        arrayList3.clear()

        arrayList = intent.getParcelableArrayListExtra<DataKursus>("arrayList")!!
        arrayList2 = intent.getParcelableArrayListExtra<DataKursus>("arrayList2")!!
        arrayList3 = intent.getParcelableArrayListExtra<DataKursus>("arrayList3")!!
        kategori1 = intent.getStringExtra("kategori1")!!
        kategori2 = intent.getStringExtra("kategori2")!!

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

        val pagerAdapter = PagerAdapter(supportFragmentManager, arrayList, arrayList2, arrayList3, kategori1, kategori2)
        val pager = findViewById<View>(R.id.pager) as ViewPager
        pager.adapter = pagerAdapter
        tabLayout1.setupWithViewPager(pager)

        btnSearch = main_constraint.findViewById(R.id.btn_search)
        btnAkun = main_constraint.findViewById(R.id.btn_akun)
        btnKeranjang = main_constraint.findViewById(R.id.btn_keranjang)

        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        btnKeranjang.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val name = intent.getStringExtra("username")
            val id = user.uid
            userId = id
            main_progressBar.visibility = View.VISIBLE
            loadUser()
            val email2 = user.email
            if(!TextUtils.isEmpty(name)){
                if(!(name!!.contains("@"))){
                    val db = FirebaseFirestore.getInstance()
                    val userDetail = UserDetail(name, email2.toString(), "kosong", "0", "kosong", "0")
                    db.collection("users2").document(id).set(userDetail)
                }
            }
        }
        else{
            main_progressBar.visibility = View.GONE
            Glide.with(applicationContext)
                .load(resources.getDrawable(R.drawable.login))
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL))
                .into(btnAkun)
            btnAkun.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }

            userDetail = UserDetail("kosong", "kosong", "kosong", "kosong", "kosong", "kosong")
        }
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val name = intent.getStringExtra("username")
            val id = user.uid
            userId = id
            main_progressBar.visibility = View.VISIBLE
            loadUser()
            val email2 = user.email
            if(!TextUtils.isEmpty(name)){
                if(!(name!!.contains("@"))){
                    val db = FirebaseFirestore.getInstance()
                    val userDetail = UserDetail(name, email2.toString(), "kosong", "0", "kosong", "0")
                    db.collection("users2").document(id).set(userDetail)
                }
            }
        }
        else{
            main_progressBar.visibility = View.GONE
            Glide.with(applicationContext)
                .load(resources.getDrawable(R.drawable.login))
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL))
                .into(btnAkun)
            btnAkun.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }

            userDetail = UserDetail("kosong", "kosong", "kosong", "kosong", "kosong", "kosong")
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        else{
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show()
        }

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
            doubleBackToExitPressedOnce = false
        }, 2000)
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
                    result.getString("jumlahKeranjang").toString())

                if(userDetail.isiKeranjang.isNotEmpty()){
                    dataUser = UserDetail(
                        userDetail.username,
                        userDetail.email,
                        userDetail.gambar,
                        userDetail.saldo,
                        userDetail.isiKeranjang,
                        userDetail.jumlahKeranjang
                    )
                    if(userDetail.gambar != "kosong"){
                        Glide.with(applicationContext)
                            .load(userDetail.gambar)
                            .apply(
                                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                                    Target.SIZE_ORIGINAL))
                            .into(btnAkun)
                    }
                    else{
                        Glide.with(applicationContext)
                            .load(resources.getDrawable(R.drawable.akun))
                            .apply(
                                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                                    Target.SIZE_ORIGINAL))
                            .into(btnAkun)
                    }
                    btnAkun.setOnClickListener {
                        val intent2 = Intent(this, AkunActivity::class.java)
                        startActivity(intent2)
                    }
                    main_progressBar.visibility = View.GONE
                }
                else{
                    loadUser()
                }
            }
            .addOnFailureListener { exception ->
                main_progressBar.visibility = View.GONE
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
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
