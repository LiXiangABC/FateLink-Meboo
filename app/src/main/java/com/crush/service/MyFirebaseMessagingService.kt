package com.crush.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.crush.App
import com.crush.Constant
import com.crush.R
import com.crush.dialog.PushInsideDialog
import com.crush.entity.BaseEntity
import com.crush.rongyun.RongConfigUtil
import com.crush.ui.HomeActivity
import com.crush.util.ImageLoaderUtils
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.rong.imkit.SpName


class MyFirebaseMessagingService : FirebaseMessagingService() {  // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        Log.e(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
            if (needsToBeScheduled()) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }
        //type 1 回复消息  2普通照片  3私密照片  4普通视频  5私密视频 6wlm喜欢
        //isNotification true
        //targetId 目标的userCode
        if (remoteMessage.data.isEmpty()) {
            // Check if message contains a notification payload.
            remoteMessage.notification?.let {
                Log.e(TAG, "Message Notification Body: ${it.body}")
                sendNotification(it)
            }
        } else {
            if (remoteMessage.data["isNotification"] != null && remoteMessage.data["isNotification"].equals(
                    "true"
                )
            ) {
                remoteMessage.notification?.let {
                    Log.e(TAG, "Message Notification Body: ${it.body}")
                    sendNotification(it)
                }
            } else {
                Thread {
                    // 在非UI线程执行耗时操作
                    if (remoteMessage.data["type"].toString() == "19") {
                        remoteMessage.notification?.let { it1 ->
                            it1.body?.let {
                                if (it.contains("|")) {
                                    val userCodeList = it.split("|")
                                    for (i in userCodeList.indices) {
                                        RongConfigUtil.deleteRongUserHistory(userCodeList[i])
                                    }
                                } else {
                                    RongConfigUtil.deleteRongUserHistory(it)
                                }
                            }


                        }
                    } else {

                        // 使用Handler更新UI
                        val handler = Handler(Looper.getMainLooper())
                        handler.post(Runnable { // 在主线程更新UI
                            if (BaseConfig.getInstance.getString(SpName.token, "") != "") {
                                when (remoteMessage.data["type"]) {
                                    "6", "8", "18" -> {
                                        App.applicationContext()?.apply {
                                            PushInsideDialog(this, remoteMessage).showPopupWindow()
                                        }
                                    }
                                }
                            }

                        })
                    }

                }.start()

            }

        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private fun needsToBeScheduled() = true

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance(this)
            .beginWith(work)
            .enqueue()
        // [END dispatch_job]
    }

    private fun handleNow() {
        Log.e(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.e(TAG, "sendRegistrationToServer: $token")
        token?.let {
            if (!SDActivityManager.instance.isEmpty) {
                if (BaseConfig.getInstance.getString(SpName.token, "") != "") {
                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                        override fun addBody(requestBody: OkHttpBodyEntity) {
                            requestBody.setPost(Constant.user_push_token_url)
                            requestBody.add("sourceType", 1)
                            requestBody.add("token", token)
                        }

                    }, object : SDOkHttpResoutCallBack<BaseEntity>(false) {
                        override fun onSuccess(entity: BaseEntity) {

                        }

                    })
                }
            }
        }
    }

    private fun sendNotification(notification: RemoteMessage.Notification) {
        val className = HomeActivity::class.java
        val intent = Intent(this, className)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setColor(Color.parseColor("#F1399C"))
            .setColorized(true)
            .setLargeIcon(
                if (notification.imageUrl == null) null else ImageLoaderUtils().returnBitMap(
                    notification.imageUrl.toString()
                )
            )
            .setSmallIcon(if (Build.MANUFACTURER == "Google" || Build.MANUFACTURER == "Xiaomi") R.drawable.icon_notification_white else R.drawable.icon_ciccle_launch)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(false)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)

        val build = notificationBuilder.build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, build)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
        override fun doWork(): Result {
            // TODO(developer): add long running task here.
            return Result.success()
        }
    }
}