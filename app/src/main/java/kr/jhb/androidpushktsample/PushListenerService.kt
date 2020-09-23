package kr.jhb.androidpushktsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val notificationClient = MainActivity.pinpointManager?.notificationClient
        val notificationDetails = NotificationDetails.builder()
            .from(remoteMessage.from)
            .mapData(remoteMessage.data)
            .intentAction(NotificationClient.FCM_INTENT_ACTION)
            .build()

        val pushResult = notificationClient?.handleNotificationReceived(notificationDetails)
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