package com.example.kwuapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invoice.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class InvoiceActivity : AppCompatActivity() {

    private var userDetail: UserDetail? = null
    private var pesanan: Pesanan? = null
    private var userid: String? = null
    private var remainWaktu: Long = 0
    private var cdt: CountDownTimer? = null
    private lateinit var dbReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        supportActionBar?.hide()

        userDetail = intent.getParcelableExtra("akun")
        userid = intent.getStringExtra("userid")
        val selamat = "Selamat ${userDetail?.username}, Pesanan kamu telah berhasil dibuat!"
        tv_invoice_selamat.text = selamat
        val selesaikan = "Selesaikan pembayaran sebelum"
        tv_invoice_selesaikan.text = selesaikan
        tv_invoice_waktu.text = ""

        dbReference = FirebaseDatabase.getInstance().getReference("statusBayar")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data: DataSnapshot in dataSnapshot.children){
                    val hasil = data.getValue(Pesanan::class.java)
                    if(hasil?.status == "batal"){
                        loadPesanan()
                    }
                    else if(hasil?.status == "selesai"){
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("statusBayar").document(userid!!)
                            .update("waktu", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userid!!)
                            .update("durasi", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userid!!)
                            .update("status", "selesai")
                            .addOnSuccessListener { result2 ->
                                loadPesanan()
                            }
                            .addOnFailureListener { exception ->
                            }
                    }
                    else if(hasil?.status == "pending"){
                        loadPesanan()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference.addValueEventListener(postListener)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cdt?.cancel()
    }

    private fun loadPesanan(){
        val db = FirebaseFirestore.getInstance()
        db.collection("statusBayar").document(userid!!)
            .get()
            .addOnSuccessListener { result ->
                pesanan = Pesanan(result.getString("caraBayar"),
                    result.getLong("durasi"),
                    result.getLong("jumlah"),
                    result.getString("status"),
                    result.getLong("waktu"))

                if(pesanan != null){
                    if(pesanan?.waktu?.toInt() == 0 && pesanan?.status.toString() == "pending"){
                        remainWaktu = System.currentTimeMillis()
                        val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val dateArray = date.split("-")
                        val tahun = dateArray[0].toInt()
                        val bulan = dateArray[1].toInt() - 1
                        val tanggal = dateArray[2].toInt() + 1
                        val end_calendar: Calendar = Calendar.getInstance()
                        val start_millis: Long = remainWaktu
                        end_calendar.set(tahun, bulan, tanggal)
                        val end_millis: Long = end_calendar.getTimeInMillis() //get the end time in milliseconds
                        val total_millis = end_millis - start_millis //total time in milliseconds
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("statusBayar").document(userid!!)
                            .update("waktu", System.currentTimeMillis())
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        val db3 = FirebaseFirestore.getInstance()
                        db3.collection("statusBayar").document(userid!!)
                            .update("durasi", total_millis)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        timer(remainWaktu)
                    }
                    else if(pesanan?.status.toString() == "selesai"){
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("statusBayar").document(userid!!)
                            .update("waktu", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userid!!)
                            .update("durasi", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userid!!)
                            .update("status", "selesai")
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        Toast.makeText(this@InvoiceActivity, "Pembayaran Anda Berhasil", Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            val intent = Intent(this@InvoiceActivity, AkunActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 3000)
                    }
                    else if(pesanan?.status == "batal"){
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("statusBayar").document(userid!!)
                            .update("waktu", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userid!!)
                            .update("durasi", 0)
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        db2.collection("statusBayar").document(userid!!)
                            .update("status", "batal")
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }
                        Toast.makeText(this@InvoiceActivity, "Pesanan Anda telah Dibatalkan", Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            val intent = Intent(this@InvoiceActivity, AkunActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 3000)
                    }
                    else if(pesanan?.waktu?.toInt() != 0 && pesanan?.status.toString() == "pending"){
                        val sekarang = System.currentTimeMillis() - pesanan?.waktu!!
                        timer2(sekarang, pesanan?.durasi!!)
                    }
                }
                else{
                    loadPesanan()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun timer(time: Long){
        val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dateArray = date.split("-")
        val tahun = dateArray[0].toInt()
        val bulan = dateArray[1].toInt() - 1
        val tanggal = dateArray[2].toInt() + 1
        val end_calendar: Calendar = Calendar.getInstance()
        val start_millis: Long = time
        end_calendar.set(tahun, bulan, tanggal)
        val end_millis: Long = end_calendar.getTimeInMillis() //get the end time in milliseconds
        val total_millis = end_millis - start_millis //total time in milliseconds
        //1000 = 1 second interval
        cdt = object : CountDownTimer(total_millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val  days = TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                val hours = (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                    TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))
                val minutes =(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))
                val seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                val waktu = "$hours jam $minutes menit $seconds detik"
                tv_invoice_waktu.text = waktu
            }

            override fun onFinish() {
                val data = Pesanan("kosong",0,0,"batal",0)
                dbReference.child(userid!!).setValue(data)
            }
        }
        cdt?.start()
    }

    private fun timer2(time: Long, durasi: Long){
        val newTotal = durasi - time
        //1000 = 1 second interval
        cdt = object : CountDownTimer(newTotal, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val  days = TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                val hours = (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                    TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))
                val minutes =(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))
                val seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                val waktu = "$hours jam $minutes menit $seconds detik"
                tv_invoice_waktu.text = waktu
            }

            override fun onFinish() {
                val data = Pesanan("kosong",0,0,"batal",0)
                dbReference.child(userid!!).setValue(data)
            }
        }
        cdt?.start()
    }
}
