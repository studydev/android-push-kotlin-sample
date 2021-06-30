package kr.aws.pinpoint

import android.util.Log
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushListenerService : FirebaseMessagingService() {

    private val TAG = "PushListenerService"

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        MainActivity.pinpointManager?.notificationClient?.registerDeviceToken(token)
        Log.d(TAG,"$token registered")
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
}