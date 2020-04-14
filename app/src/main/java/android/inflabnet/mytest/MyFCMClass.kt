package android.inflabnet.mytest

import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMClass: FirebaseMessagingService() {

    private val TAG = "JSA-FCM"

    override fun onNewToken(token: String) {
        Log.i("Teste", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            // do with Notification payload...
            // remoteMessage.notification.body

            Log.e(TAG, "Title: " + remoteMessage.notification?.title!!)
            Log.e(TAG, "Body: " + remoteMessage.notification?.body!!)
        }

        if (remoteMessage.data.isNotEmpty()) {
            // do with Data payload...
            // remoteMessage.data
            Log.e(TAG, "Data: " + remoteMessage.data)
        }
    }
}