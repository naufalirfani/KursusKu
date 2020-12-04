package com.example.kwuapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.work.*
import androidx.work.Data.Builder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout1: TabLayout
    private var arrayList: ArrayList<DataKursus>? = arrayListOf()
    private var arrayList2: ArrayList<DataKursus>? = arrayListOf()
    private var arrayList3: ArrayList<DataKursus>? = arrayListOf()
    private var angka1: Int = 0
    private var angka2: Int = 0
    private var kategori1: String? = null
    private var kategori2: String? = null
    private val kategori = arrayOf("Desain", "Bisnis", "Finansial", "Kantor", "Pendidikan", "Pengembangan")

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
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        arrayList?.clear()
        arrayList2?.clear()
        arrayList3?.clear()

        Glide.with(this).load(R.drawable.bouncy_balls).into(main_progressBar)

        arrayList = intent.getParcelableArrayListExtra<DataKursus>("arrayList")
        arrayList2 = intent.getParcelableArrayListExtra<DataKursus>("arrayList2")
        arrayList3 = intent.getParcelableArrayListExtra<DataKursus>("arrayList3")
        kategori1 = intent.getStringExtra("kategori1")
        kategori2 = intent.getStringExtra("kategori2")

        tabLayout1 = findViewById<View>(R.id.tabLayout) as TabLayout
        tabLayout1.addTab(tabLayout1.newTab().setText("BERANDA"))
        tabLayout1.addTab(tabLayout1.newTab().setText("KATEGORi"))
        tabLayout1.setTabTextColors(Color.parseColor("#BDBDBD"), Color.parseColor("#000000"))

        val pagerAdapter = PagerAdapter(supportFragmentManager, arrayList!!, arrayList2!!, arrayList3!!, kategori1!!, kategori2!!)
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
            getTokenFCM()
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


    override fun onDestroy() {
        super.onDestroy()
        startPeriodicTask()
    }


    fun getTokenFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            val msg = token
            val db2 = FirebaseFirestore.getInstance()
            db2.collection("users2").document(userId)
                .update("isiKeranjang", msg)
                .addOnSuccessListener { result2 ->
                }
                .addOnFailureListener { exception ->
                }
        })
    }

    override fun onResume() {
        super.onResume()
        loadKursus()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val name = intent.getStringExtra("username")
            val id = user.uid
            userId = id
            main_progressBar.visibility = View.VISIBLE
            loadUser()
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
        }, 1500)
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
                    result.getString("jumlahKeranjang").toString(),
                    result.getString("wa").toString())

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

    private fun loadKursus(){
        main_progressBar.visibility = View.VISIBLE
        randomAngka()
        val kategori1 = kategori[angka1]
        val kategori2 = kategori[angka2]
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .orderBy("dilihat", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                arrayList?.clear()
                arrayList2?.clear()
                arrayList3?.clear()
                for (document in result) {
                    arrayList?.add(DataKursus(document.getString("deskripsi")!!,
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

                if(arrayList != null){
                    for(i in 0 until arrayList!!.size){
                        if(arrayList!![i].kategori == kategori1){
                            arrayList2!!.add(arrayList!![i])
                        }
                        if(arrayList!![i].kategori == kategori2){
                            arrayList3!!.add(arrayList!![i])
                        }
                    }

                    main_progressBar.visibility = View.GONE
                    val pagerAdapter = PagerAdapter(supportFragmentManager, arrayList!!, arrayList2!!, arrayList3!!,
                        kategori1,
                        kategori2
                    )
                    val pager = findViewById<View>(R.id.pager) as ViewPager
                    pager.adapter = pagerAdapter
                    tabLayout1.setupWithViewPager(pager)
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting documents: ", exception)
                Toast.makeText(this, "Koneksi error", Toast.LENGTH_SHORT).show()
            }
    }

    fun randomAngka(){
        angka1 = Random.nextInt(0,5)
        angka2 = Random.nextInt(0,5)
        if(angka1 == angka2){
            randomAngka()
        }
    }

    private fun startPeriodicTask() {
        val data = Builder()
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 5, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueue(periodicWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this@MainActivity,
            Observer<WorkInfo> { workInfo ->
                val status = workInfo.state.name
                Toast.makeText(this@MainActivity, status, Toast.LENGTH_SHORT).show()
            })
    }

    private fun cancelPeriodicTask() {
        WorkManager.getInstance().cancelWorkById(periodicWorkRequest.id)
    }
}
