package com.example.kwuapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.*

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private lateinit var dbReference: DatabaseReference

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val APP_ID = "YOUR_KEY_HERE"
        const val EXTRA_CITY = "city"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }
    private var resultStatus: Result? = null

    override fun doWork(): Result {
        val result = getCurrentWeather()
        return result
    }

    private fun getCurrentWeather(): Result {
        dbReference = FirebaseDatabase.getInstance().getReference("coba")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for( data in dataSnapshot.children){
                    val hasil = data.getValue(DataKeranjang::class.java)
                    showNotification(hasil?.namaKursus!!, hasil.jumlah.toString(), 2)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        dbReference.addValueEventListener(postListener)
        return resultStatus as Result
    }

    private fun showNotification(title: String, message: String, notifId: Int) {

        val CHANNEL_ID = "Channel_01"
        val CHANNEL_NAME = "KursusKu channel"

        val intent = Intent(applicationContext, AkunActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logokursuskusmall2)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))
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

            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManager.notify(notifId, notification)

    }
}