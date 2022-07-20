package com.samoa.samoa_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.samoa.hearth_sdk.HearthDeviceSdk
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var mHearthDeviceSdk: HearthDeviceSdk? = null
    private var connected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mHearthDeviceSdk = HearthDeviceSdk.createNewInstance(this)

        client_connect_button.setOnClickListener {
            if (connected) {
                mHearthDeviceSdk?.disconnectToRemoteService()
                connected = false
                client_message_edit.visibility = View.GONE
                client_send_url_button.visibility = View.GONE
                client_connect_button.text = "Connect"
            } else {
                mHearthDeviceSdk?.connectToRemoteService()
                connected = true
                client_message_edit.visibility = View.VISIBLE
                client_send_url_button.visibility = View.VISIBLE
                client_connect_button.text = "Disconnect"
            }
        }

        client_send_url_button.setOnClickListener {
            if (client_message_edit.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter your message or url", Toast.LENGTH_SHORT).show()
            } else {
                mHearthDeviceSdk?.installAppPackage(
                    url = client_message_edit.text.toString().trim(),
                    md5Sum = "md5Sum",
                    object : HearthDeviceSdk.CallBackInstallApp {
                        override fun onSuccess(functionName: String, success: String) {
                            Log.i("onSuccess", "functionName: $functionName  success: $success")
                        }

                        override fun onFailure(functionName: String, exception: Exception) {
                            Log.i("onFailure", "functionName: $functionName  exception: ${exception.printStackTrace()}")
                        }

                    })
            }
        }
    }
}