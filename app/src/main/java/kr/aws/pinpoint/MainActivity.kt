package kr.aws.pinpoint

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        getPinpointManager(applicationContext)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult>{
                    override fun onComplete(task: Task<InstanceIdResult>) {
                        if (!task.isSuccessful) {
                            Log.w(TAG, "getInstanceId failed", task.exception)
                            return
                        }
                        val token = task.getResult().token
                        Log.d(TAG, "Registering push notification token: $token")
                        pinpointManager?.notificationClient?.registerDeviceToken(token)
                    }

                })

    }

    fun getPinpointManager(applicationContext: Context): PinpointManager {
        if (pinpointManager != null)
            return pinpointManager as PinpointManager

        val awsConfig = AWSConfiguration(applicationContext)
        val awsClient = AWSMobileClient.getInstance()

        awsClient.initialize(applicationContext, awsConfig, object : Callback<UserStateDetails>{
            override fun onResult(result: UserStateDetails?) {
                Log.i(TAG, result?.userState.toString())
            }

            override fun onError(e: Exception?) {
                Log.e(TAG, "Initialization error.", e)
            }

        })

        val pinpointConfig = PinpointConfiguration(
            applicationContext,
            AWSMobileClient.getInstance(),
            awsConfig
        )

        pinpointManager = PinpointManager(pinpointConfig)
        return pinpointManager as PinpointManager
    }


    companion object {
        var pinpointManager: PinpointManager? = null
    }

}
