package kr.jhb.androidpushktsample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.text.parseAsHtml
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient.INTENT_SNS_NOTIFICATION_DATA
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient.INTENT_SNS_NOTIFICATION_FROM
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushListenerService : FirebaseMessagingService() {

    private val TAG = "PushListenerService"

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        Log.d(TAG,"Registring push notifications token: $token")
        MainActivity.pinpointManager?.notificationClient?.registerDeviceToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message : $remoteMessage.data")

//        val notificationClient = MainActivity.pinpointManager?.notificationClient
//        val notificationDetails = NotificationDetails.builder()
//            .from(remoteMessage.from)
//            .mapData(remoteMessage.data)
//            .intentAction(NotificationClient.FCM_INTENT_ACTION)
//            .build()
//
//        val pushResult = notificationClient?.handleNotificationReceived(notificationDetails)

        val title = remoteMessage.data["pinpoint.notification.title"] ?: ""
        val body = remoteMessage.data["pinpoint.notification.body"] ?: ""

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default-channel-id"
        val channelName = "default-channel-name"
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
            }
        )
        val notifyBuilder = Notification.Builder(this, channelId).apply {
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.ic_launcher_background)
            setContentTitle(title.parseAsHtml())
        }
        notificationManager.notify((System.currentTimeMillis() / 1000).toInt(), notifyBuilder.build())


    }

    private fun broadcast(from: String?, dataMap: HashMap<String, String>) {
        val intent = Intent(ACTION_PUSH_NOTIFICATION)
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from)
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {
        // Intent action used in local broadcast
        const val ACTION_PUSH_NOTIFICATION = "push-notification"

        /**
         * Helper method to extract push message from bundle.
         *
         * @param data bundle
         * @return message string from push notification
         */
        fun getMessage(data : Bundle): String {
            return data.get("data").toString()
        }
    }
}