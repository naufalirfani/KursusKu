package com.warnet.kursusku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_screen.*
import kotlin.random.Random

class ScreenActivity : AppCompatActivity() {

    private var arrayList = ArrayList<DataKursus>()
    private var arrayList2 = ArrayList<DataKursus>()
    private var arrayList3 = ArrayList<DataKursus>()
    private val kategori =
        arrayOf("Desain", "Bisnis", "Finansial", "Kantor", "Pendidikan", "Pengembangan")
    private var angka1: Int = 0
    private var angka2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)

        supportActionBar?.hide()

        val versi = "Versi " + BuildConfig.VERSION_NAME
        tv_screen_versi.text = versi

        loadKursus()
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
                Toast.makeText(this, "Koneksi error", Toast.LENGTH_SHORT).show()
            }
    }

    fun randomAngka() {
        angka1 = Random.nextInt(0, 5)
        angka2 = Random.nextInt(0, 5)
        if (angka1 == angka2) {
            randomAngka()
        }
    }
}
