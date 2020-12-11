package com.WarnetIT.kursusku

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
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
    private lateinit var btnKeranjang: ImageView
    private lateinit var jumlahKeranjang: TextView
    private lateinit var btnSearch: Button
    private var doubleBackToExitPressedOnce = false
    private lateinit var dbReference: DatabaseReference
    private var dataPesanan: DataPesanan? = null
    private lateinit var dbReference2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        arrayList?.clear()
        arrayList2?.clear()
        arrayList3?.clear()


        Glide.with(this).load(R.drawable.bouncy_balls).into(main_progressBar)

        dbReference2 = FirebaseDatabase.getInstance().getReference("keranjang")

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
        jumlahKeranjang = main_constraint.findViewById(R.id.tv_main_jumlahkeranjang)
        btnKeranjang = main_constraint.findViewById(R.id.btn_keranjang)

        jumlahKeranjang.visibility = View.GONE

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
            jumlahKeranjang()
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
            jumlahKeranjang()
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
                    loadStatusBayar(userDetail)
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

    private fun loadStatusBayar(user: UserDetail){
        dbReference = FirebaseDatabase.getInstance().getReference("statusBayar").child(userId)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hasil = dataSnapshot.getValue(DataPesanan::class.java)
                when (hasil?.status) {
                    "selesai" -> {
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("statusBayar").document(userId)
                            .update("waktu", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userId)
                            .update("durasi", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userId)
                            .update("status", "selesai")
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                    }
                }
                loadPesanan(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference.addValueEventListener(postListener)
    }

    private fun loadPesanan(user: UserDetail){
        val db = FirebaseFirestore.getInstance()
        db.collection("statusBayar").document(userId)
            .get()
            .addOnSuccessListener { result ->
                dataPesanan = DataPesanan(result.getString("caraBayar"),
                    result.getLong("durasi"),
                    result.getLong("jumlah"),
                    result.getString("status"),
                    result.getLong("waktu"))

                if(dataPesanan != null){
                    if(dataPesanan?.status.toString() == "selesai"){
                        showNotification("Pembayaran Berhasil", "Selamat! Pembayaran Kamu Berhasil. Yuk, telusuri kursus keinginanmu!",
                            MyWorker.NOTIFICATION_ID
                        )
                        val data = DataPesanan("kosong",0,dataPesanan?.jumlah,"kosong",0)
                        dbReference.setValue(data)

                        val saldo = user.saldo.toLong() + dataPesanan?.jumlah!!
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("users2").document(userId)
                            .update("saldo", saldo.toString())
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userId)
                            .update("status", "kosong")
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                    }
                }
                else{
                    loadPesanan(user)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun jumlahKeranjang(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var jumlah: Long = 0
                for(data in dataSnapshot.children){
                    val hasil = data.getValue(DataKeranjang::class.java)
                    if(hasil == null){
                        jumlahKeranjang.visibility = View.GONE
                    }
                    else{
                        jumlahKeranjang.visibility = View.VISIBLE
                        jumlah += hasil.jumlah!!
                    }
                }
                jumlahKeranjang.text = jumlah.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference2.child(userId).addValueEventListener(postListener)
    }

    private fun showNotification(title: String, message: String, notifId: Int) {
        val intent = Intent(applicationContext, AkunActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext, MyWorker.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_white)
            .setLargeIcon((BitmapFactory.decodeResource(this.resources, R.drawable.logokursuskusmall2)))
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                MyWorker.CHANNEL_ID,
                MyWorker.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            builder.setChannelId(MyWorker.CHANNEL_ID)

            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManager.notify(notifId, notification)

    }
}
