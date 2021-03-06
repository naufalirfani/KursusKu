package com.warnet.kursusku

import android.app.AlertDialog
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_akun.*
import java.lang.Exception
import kotlin.random.Random


@Suppress("DEPRECATION")
class AkunActivity : AppCompatActivity() {

    private lateinit var userDetail: UserDetail
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private lateinit var dbReference: DatabaseReference
    private var dataPesanan: DataPesanan? = null

    private var arrayList = ArrayList<DataKursus>()
    private var arrayList2 = ArrayList<DataKursus>()
    private var arrayList3 = ArrayList<DataKursus>()
    private val kategori =
        arrayOf("Desain", "Bisnis", "Finansial", "Kantor", "Pendidikan", "Pengembangan")
    private var angka1: Int = 0
    private var angka2: Int = 0
    private lateinit var dbReference2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun)

        supportActionBar?.hide()

        Glide.with(this).load(R.drawable.bouncy_balls).into(akun_progressbar)
        Glide.with(this).load(R.drawable.bouncy_balls).into(akun_progressbar_utama)
        akun_progressbar_utama.visibility = View.GONE
        akun_progressbar.bringToFront()

        dbReference2 = FirebaseDatabase.getInstance().getReference("keranjang")

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            jumlahKeranjang()
        }

        btn_akun_back.setOnClickListener { loadKursus() }

        btn_akun_keranjang.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }

        btn_akun_keluar.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setMessage("Apakah Anda ingin keluar?")

            builder.setPositiveButton(
                "Ya"
            ) { dialog, which -> // Do nothing but close the dialog
                akun_progressbar_utama.visibility = View.VISIBLE
                FirebaseAuth.getInstance().signOut()

                try {
                    disconnectFromFacebook()
                } catch (e: Exception) {
                }
                val handler = Handler()
                handler.postDelayed({
                    onBackPressed()
                    akun_progressbar_utama.visibility = View.GONE
                }, 3000)
                dialog.dismiss()
            }

            builder.setNegativeButton(
                "Tidak"
            ) { dialog, which -> // Do nothing
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.setOnShowListener(OnShowListener {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.colorAbuGelap))
            })
            alert.show()
        }
        loadUser2()

        btn_akun_chat.setOnClickListener {
            Toast.makeText(this, "Masih dalam Tahap Pengembangan.", Toast.LENGTH_SHORT).show()
        }

        btn_akun_history.setOnClickListener {
            Toast.makeText(this, "Masih dalam Tahap Pengembangan.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            loadKursus()
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        akun_progressbar.visibility = View.GONE
        tv_akun_jumlahkeranjang.visibility = View.GONE
        loadUser2()
        jumlahKeranjang()
    }

    fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    }

    private fun loadUser() {
        if (userDetail.gambar != "kosong") {
            Glide.with(applicationContext)
                .load(userDetail.gambar)
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL
                    )
                )
                .into(iv_akun_foto)
        } else {
            Glide.with(applicationContext)
                .load(resources.getDrawable(R.drawable.akun))
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL
                    )
                )
                .into(iv_akun_foto)
        }
        tv_akun_username.text = userDetail.username
        tv_akun_email.text = userDetail.email
        if (userDetail.saldo.length in 4..6) {
            var x = userDetail.saldo
            x = x.substring(0, x.length - 3) + "." + x.substring(x.length - 3, x.length)
            val textHarga = "Rp $x"
            tv_akun_saldo.text = textHarga
        } else if (userDetail.saldo.length in 7..9) {
            var x = userDetail.saldo
            x = x.substring(0, x.length - 3) + "." + x.substring(x.length - 3, x.length)
            x = x.substring(0, x.length - 7) + "." + x.substring(x.length - 7, x.length)
            val textHarga = "Rp $x"
            tv_akun_saldo.text = textHarga
        } else
            if (userDetail.saldo.length in 10..12) {
                var x = userDetail.saldo
                x = x.substring(0, x.length - 3) + "." + x.substring(x.length - 3, x.length)
                x = x.substring(0, x.length - 7) + "." + x.substring(x.length - 7, x.length)
                x = x.substring(0, x.length - 11) + "." + x.substring(x.length - 11, x.length)
                val textHarga = "Rp $x"
                tv_akun_saldo.text = textHarga
            } else {
                val saldo = "Rp ${userDetail.saldo}"
                tv_akun_saldo.text = saldo
            }
    }

    private fun loadUser2() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users2").document(userId)
            .get()
            .addOnSuccessListener { result ->
                userDetail = UserDetail(
                    result.getString("username").toString(),
                    result.getString("email").toString(),
                    result.getString("gambar").toString(),
                    result.getString("saldo").toString(),
                    result.getString("isiKeranjang").toString(),
                    result.getString("jumlahKeranjang").toString(),
                    result.getString("wa").toString()
                )

                if (userDetail.isiKeranjang.isNotEmpty()) {
                    btn_akun_setting.setOnClickListener {
                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("userDetail", userDetail)
                        startActivity(intent)
                    }

                    btn_akun_setting2.setOnClickListener {
                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("userDetail", userDetail)
                        startActivity(intent)
                    }
                    iv_akun_foto.setOnClickListener {
                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("userDetail", userDetail)
                        startActivity(intent)
                    }
                    loadPesanan(userDetail, userId)

                    loadUser()
                } else {
                    loadUser2()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Connection error.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPesanan(user: UserDetail, id: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("statusBayar").document(userId)
            .get()
            .addOnSuccessListener { result ->
                dataPesanan = DataPesanan(
                    result.getString("caraBayar"),
                    result.getLong("durasi"),
                    result.getLong("jumlah"),
                    result.getString("status"),
                    result.getLong("waktu")
                )

                if (dataPesanan != null) {
                    akun_progressbar.visibility = View.GONE
                    when (dataPesanan?.status) {
                        "batal" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, TopUpActivity::class.java)
                                intent.putExtra("akun", user)
                                intent.putExtra("userid", id)
                                startActivity(intent)
                            }
                        }
                        "selesai" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, TopUpActivity::class.java)
                                intent.putExtra("akun", user)
                                intent.putExtra("userid", id)
                                startActivity(intent)
                            }
                        }
                        "pending" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, InvoiceActivity::class.java)
                                intent.putExtra("akun", user)
                                intent.putExtra("userid", id)
                                startActivity(intent)
                            }
                        }
                        "kosong" -> {
                            btn_akun_isisaldo.setOnClickListener {
                                val intent = Intent(applicationContext, TopUpActivity::class.java)
                                intent.putExtra("akun", user)
                                intent.putExtra("userid", id)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    loadPesanan(user, id)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Connection error.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadKursus() {
        randomAngka()
        val kategori1 = kategori[angka1]
        val kategori2 = kategori[angka2]
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .orderBy("dilihat", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                arrayList2.clear()
                arrayList3.clear()
                for (document in result) {
                    arrayList.add(
                        DataKursus(
                            document.getString("deskripsi")!!,
                            document.getLong("dilihat")!!,
                            document.getString("gambar")!!,
                            document.getString("harga")!!,
                            document.getString("kategori")!!,
                            document.getString("nama")!!,
                            document.getString("pembuat")!!,
                            document.getLong("pengguna")!!,
                            document.getString("rating")!!,
                            document.getString("remaining")!!,
                            document.getString("video")!!
                        )
                    )
                }

                if (arrayList.isNotEmpty()) {
                    for (i in 0 until arrayList.size) {
                        if (arrayList[i].kategori == kategori1) {
                            arrayList2.add(arrayList[i])
                        }
                        if (arrayList[i].kategori == kategori2) {
                            arrayList3.add(arrayList[i])
                        }
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("arrayList", arrayList)
                    intent.putExtra("arrayList2", arrayList2)
                    intent.putExtra("arrayList3", arrayList3)
                    intent.putExtra("kategori1", kategori1)
                    intent.putExtra("kategori2", kategori2)
                    startActivity(intent)
                    finish()
                } else {
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Koneksi error.", Toast.LENGTH_SHORT).show()
            }
    }

    fun randomAngka() {
        angka1 = Random.nextInt(0, 5)
        angka2 = Random.nextInt(0, 5)
        if (angka1 == angka2) {
            randomAngka()
        }
    }

    private fun jumlahKeranjang() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var jumlah: Long = 0
                for (data in dataSnapshot.children) {
                    val hasil = data.getValue(DataKeranjang::class.java)
                    if (hasil == null) {
                        tv_akun_jumlahkeranjang.visibility = View.GONE
                    } else {
                        tv_akun_jumlahkeranjang.visibility = View.VISIBLE
                        jumlah += hasil.jumlah!!
                    }
                }
                tv_akun_jumlahkeranjang.text = jumlah.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference2.child(userId).addValueEventListener(postListener)
    }
}
