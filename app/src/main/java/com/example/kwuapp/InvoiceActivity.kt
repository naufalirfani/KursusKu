package com.example.kwuapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequest
import androidx.lifecycle.Observer
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invoice.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class InvoiceActivity : AppCompatActivity() {

    private var userDetail: UserDetail? = null
    private var dataPesanan: DataPesanan? = null
    private var userid: String? = null
    private var remainWaktu: Long = 0
    private var cdt: CountDownTimer? = null
    private lateinit var dbReference: DatabaseReference
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

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
        tv_invoice_hpwa.text = userDetail?.wa
        tv_invoice_bayar.text = ""
        tv_invoice_jumlah.text = getString(R.string.jumlah_pembayaran)
        tv_invoice_harga.text = ""

        Glide.with(this)
            .load(R.drawable.bouncy_balls)
            .into(invoice_progressbar)

        dbReference = FirebaseDatabase.getInstance().getReference("statusBayar").child(userid!!)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hasil = dataSnapshot.getValue(DataPesanan::class.java)
                when (hasil?.status) {
                    "batal" -> {
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
                    }
                    "selesai" -> {
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
                    }
                }
                loadPesanan()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dbReference.addValueEventListener(postListener)

        startPeriodicTask()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent  = Intent(this, AkunActivity::class.java)
        startActivity(intent)
        finish()
        cdt?.cancel()
    }

    private fun caraBayar(cara: String){
        when (cara) {
            "bri" -> {
                iv_invoice_bayar.setImageResource(R.drawable.bri)
                tv_invoice_bayar.text = "2290-01-002180-50-8"
            }
            "mandiri" -> {
                iv_invoice_bayar.setImageResource(R.drawable.mandiri)
                tv_invoice_bayar.text = "1370-0166-1237-2"
            }
            "indosat" -> {
                iv_invoice_bayar.setImageResource(R.drawable.indosat)
                tv_invoice_bayar.text = "0857-8578-2582"
            }
            "tsel" -> {
                iv_invoice_bayar.setImageResource(R.drawable.tsel)
                tv_invoice_bayar.text = "0823-3199-7759"
            }
        }
    }

    private fun loadPesanan(){
        val db = FirebaseFirestore.getInstance()
        db.collection("statusBayar").document(userid!!)
            .get()
            .addOnSuccessListener { result ->
                dataPesanan = DataPesanan(result.getString("caraBayar"),
                    result.getLong("durasi"),
                    result.getLong("jumlah"),
                    result.getString("status"),
                    result.getLong("waktu"))

                if(dataPesanan != null){
                    invoice_progressbar.visibility = View.GONE
                    val totalHarga = dataPesanan?.jumlah
                    if(totalHarga.toString().length > 3){
                        var x = totalHarga.toString()
                        x = x.substring(0, x.length-3) + "." + x.substring(x.length -3, x.length)
                        val textHarga = "Rp $x"
                        tv_invoice_harga.text = textHarga
                    }
                    else{
                        val textHarga = "Rp $totalHarga"
                        tv_invoice_harga.text = textHarga
                    }

                    caraBayar(dataPesanan?.caraBayar!!.toString())

                    if(dataPesanan?.waktu?.toInt() == 0 && dataPesanan?.status.toString() == "pending"){
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
                    else if(dataPesanan?.status.toString() == "selesai"){
                        cancelPeriodicTask()
                        invoice_progressbar.visibility = View.VISIBLE
                        showNotification("Pembayaran Berhasil", "Selamat! Pembayaran Kamu Berhasil. Yuk, telusuri kursus keinginanmu!", 1)
                        Toast.makeText(this@InvoiceActivity, "Pembayaran Berhasil", Toast.LENGTH_SHORT).show()

                        val data = DataPesanan("kosong",0,dataPesanan?.jumlah,"selesai",0)
                        dbReference.setValue(data)

                        val saldo = userDetail?.saldo!!.toLong() + dataPesanan?.jumlah!!
                        val db2 = FirebaseFirestore.getInstance()
                        db2.collection("users2").document(userid!!)
                            .update("saldo", saldo.toString())
                            .addOnSuccessListener { result2 ->
                            }
                            .addOnFailureListener { exception ->
                            }

                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            invoice_progressbar.visibility = View.GONE
                            val intent = Intent(this@InvoiceActivity, AkunActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 3000)
                    }
                    else if(dataPesanan?.status == "batal"){
                        invoice_progressbar.visibility = View.VISIBLE
                        Toast.makeText(this@InvoiceActivity, "Pesanan Anda telah Dibatalkan", Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            invoice_progressbar.visibility = View.GONE
                            val intent = Intent(this@InvoiceActivity, AkunActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 3000)
                    }
                    else if(dataPesanan?.waktu?.toInt() != 0 && dataPesanan?.status.toString() == "pending"){
                        val sekarang = System.currentTimeMillis() - dataPesanan?.waktu!!
                        timer2(sekarang, dataPesanan?.durasi!!)
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
                val data = DataPesanan("kosong",0,0,"batal",0)
                dbReference.setValue(data)
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
                val data = DataPesanan("kosong",0,0,"batal",0)
                dbReference.setValue(data)
            }
        }
        cdt?.start()
    }

    private fun showNotification(title: String, message: String, notifId: Int) {

        val CHANNEL_ID = "Channel_01"
        val CHANNEL_NAME = "KursusKu channel"

        val intent = Intent(this, AkunActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notificationManagerCompat = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logokursuskusmall)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(this, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            builder.setChannelId(CHANNEL_ID)

            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManagerCompat.notify(notifId, notification)

    }

    private fun startPeriodicTask() {
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_UID, userid)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 5, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueue(periodicWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this,
            Observer<WorkInfo> { workInfo ->
                val status = workInfo.state.name
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            })
    }

    private fun cancelPeriodicTask() {
        WorkManager.getInstance().cancelWorkById(periodicWorkRequest.id)
    }
}
