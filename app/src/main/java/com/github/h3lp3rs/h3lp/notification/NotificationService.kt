package com.github.h3lp3rs.h3lp.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.h3lp3rs.h3lp.R

/**
 *  Static class containing useful function to throw notification.
 *  The notification are stylized with H3LP app style
 *  @warning always make sure to use {@link createNotificationChannel} method
 *  to make sure the notification channel is initialized
 */
class NotificationService {

    companion object {
        private const val CHANNEL_ID = "Help_channel_id"
        private var notificationId= 0

        /**
         * Create the notification channel for HELP application
         * Should be called before sending any notification
         * @param ctx the context of the app
         * @note If the Notification Channel is already created this function doesn't recreate it and just do nothing
         */
        fun createNotificationChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val title = ctx.resources.getString(R.string.notification_manager_title)
                val descriptiontxt =
                    ctx.resources.getString(R.string.notification_manager_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, title, importance).apply {
                    description = descriptiontxt
                }
                val notificationManager: NotificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Send simple notification without any action
         * @param ctx the context of the app
         * @param title Title on the notification
         * @param description Description on the notification
         */
        fun sendSimpleNotification(ctx:Context,title : String, description : String){
            val builder = buildBasicNotification(ctx,title,description)
            sendNotification(builder.build(),ctx)
        }

        /**
         * Send Notification with Intent triggered when clicked on
         * @param ctx the context of the app
         * @param title Title on the notification
         * @param description Description on the notification
         * @param intent the intent to trigger when the notification is clicked
         */
        fun sendIntentNotification(ctx:Context, title : String, description : String, intent : Intent){
            val pendingIntent : PendingIntent = PendingIntent.getActivity(ctx,
                FLAG_ONE_SHOT,intent , FLAG_ONE_SHOT)
            val builder = buildBasicNotification(ctx,title,description)
                .setContentIntent(pendingIntent)
            sendNotification(builder.build(),ctx)
        }

        /**
         * Send Notification opening an activity when clicked
         * @param ctx the context of the app
         * @param title Title on the notification
         * @param description Description on the notification
         * @param activity activity to open when the notification is clicked
         */
        fun <T> sendOpenActivityNotification(ctx:Context, title: String, description: String, activity: Class<T>){
            val intent= Intent(ctx, activity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            sendIntentNotification(ctx,title,description,intent)
        }

        /**
         * Send notification in with the given context
         * Take care of assigning a unique id to the notification
         * @param notification the notification to send
         * @param ctx the app context from whem to throw notification
         */
        private fun sendNotification(notification : Notification, ctx: Context){
            with(NotificationManagerCompat.from(ctx)) {
                notify(notificationId, notification)
            }
            synchronized(this){
                notificationId++
            }
        }

        /**
         * Build a stylized notification for the H3LP application
         * @param ctx the app context
         * @param title the title to add to the notification
         * @param description the description to add to the notification
         * @return a^uncompleted notificationCompat builder with the sryle of the H3LP app
         */
        private fun buildBasicNotification(ctx:Context,title : String, description : String) : NotificationCompat.Builder{
            val bigImageBitmap = BitmapFactory.decodeResource(
                ctx.resources,
                R.drawable.notification_icon
            )
            return NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon2)
                .setLargeIcon(bigImageBitmap)
                .setContentTitle(title)
                .setContentText(description)
                .setColor(ctx.getColor(R.color.red))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        }
    }
}