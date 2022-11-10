package com.example.workmangerstudy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorkManagerWithData(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return try {
            val str = inputData.getString("mKey")
            Log.i("mLog", "Success $str")
            showNotification(context)
            val outPutData: Data = Data.Builder().putString("mKey", "Hello fom Worker").build()
            Result.success(outPutData)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    val CHANNEL_ID = "com.example.workmangerstudy"

    private fun showNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            notify(1, createNotification(context))
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.resources.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(context: Context): Notification {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 2, intent, 0)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(context.resources.getString(R.string.app_name)).setContentText(context.resources.getString(R.string.app_name)).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true).build()
    }
}