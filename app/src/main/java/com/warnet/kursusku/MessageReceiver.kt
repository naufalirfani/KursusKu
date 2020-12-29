package com.warnet.kursusku

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created by What's That Lambda on 11/6/17.
 */
class MessageReceiver : FirebaseMessagingService() {

    private lateinit var dbReference: DatabaseReference
    private var title:String? = null
    private var message:String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        title = remoteMessage.data["title"]
        message = remoteMessage.data["body"]
//        showNotifications()
    }

    private fun showNotifications(){
        dbReference = FirebaseDatabase.getInstance().getReference("coba")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for( data in dataSnapshot.children){
                    val hasil = data.getValue(DataKeranjang::class.java)
                    showNotification(title!!, message!!, 2)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        dbReference.addValueEventListener(postListener)
    }

    private fun showNotification(title: String, message: String, notifId: Int) {

        val CHANNEL_ID = "Channel_01"
        val CHANNEL_NAME = "KursusKu channel"

        val intent = Intent(applicationContext, AkunActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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