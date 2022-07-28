package com.samoa.hearth_sdk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.hearthmanagement.IHearthDeviceSdk
import com.hearthmanagement.IHearthListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class HearthDeviceSdk(var context: Context) : ServiceConnection {
    var iRemoteService: IHearthDeviceSdk? = null

    private var connected = false

    companion object {
        lateinit var hearthObject: HearthDeviceSdk

        @JvmStatic
        fun createNewInstance(
            context: Context): HearthDeviceSdk {
            if (!Companion::hearthObject.isInitialized) {
                hearthObject = HearthDeviceSdk(context)
            }

            return hearthObject
        }
    }

    //for connection AIDL service
    fun connectToRemoteService() {
        try {
            if (context == null) {
                Log.i("HearthDeviceSdk", "connectToRemoteService context Null")
                return
            }
            val intent = Intent("aidlconnection")
            val pack = IHearthDeviceSdk::class.java.`package`
            pack?.let {
                intent.setPackage(pack.name)
                context.applicationContext?.bindService(
                    intent, this, Context.BIND_AUTO_CREATE
                )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    //for disconnection AIDL service
    fun disconnectToRemoteService() {
        try {
            if (connected) {
                context.applicationContext?.unbindService(this)
                connected = false
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun installAppPackage(url: String, md5Sum: String, mCallBackInstallApp: CallBackInstallApp) {
        iRemoteService?.installAppPackage(
            url,
            md5Sum,
            object : IHearthListener.Stub() {
                override fun onSuccess(functionName: String, success: String) {
                    mCallBackInstallApp.onSuccess(functionName, success)
                }

                override fun onFailure(functionName: String, message: String) {
                    if(message.equals("ChecksumVerificationFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  ChecksumVerificationFailedException())
                    }else if(message.equals("HttpDownloadingFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  HttpDownloadingFailedException())
                    }else if(message.equals("InstallAppFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  InstallAppFailedException())
                    }else if(message.equals("InstallOtaFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  InstallOtaFailedException())
                    }else if(message.equals("InternetConnectionException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  InternetConnectionException())
                    }else if(message.equals("PackageVersionVerificationFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  PackageVersionVerificationFailedException())
                    }else{
                        mCallBackInstallApp.onFailure(functionName,  throw  Exception(message))
                    }
                }
            }
        )
    }

    fun installOtaPackage(url: String, md5Sum: String, mCallBackInstallApp: CallBackInstallApp) {
        iRemoteService?.installOtaPackage(
            url,
            md5Sum,
            object : IHearthListener.Stub() {
                override fun onSuccess(functionName: String, success: String) {
                    mCallBackInstallApp.onSuccess(functionName, success)
                }

                override fun onFailure(functionName: String, message: String) {
                    if(message.equals("ChecksumVerificationFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  ChecksumVerificationFailedException())
                    }else if(message.equals("HttpDownloadingFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  HttpDownloadingFailedException())
                    }else if(message.equals("InstallAppFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  InstallAppFailedException())
                    }else if(message.equals("InstallOtaFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  InstallOtaFailedException())
                    }else if(message.equals("InternetConnectionException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  InternetConnectionException())
                    }else if(message.equals("PackageVersionVerificationFailedException")) {
                        mCallBackInstallApp.onFailure(functionName,  throw  PackageVersionVerificationFailedException())
                    }else{
                        mCallBackInstallApp.onFailure(functionName,  throw  Exception(message))
                    }
                }
            }
        )
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iRemoteService = IHearthDeviceSdk.Stub.asInterface(service)
        connected = true
        Log.i("HearthDeviceSdk", "onServiceConnected")
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        Log.i("HearthDeviceSdk", "onBindingDied "+name)
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        Log.i("HearthDeviceSdk", "onNullBinding "+name)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        iRemoteService = null
        connected = false
        Log.i("HearthDeviceSdk", "onServiceDisconnected")
    }

    fun turnOnOffScreen(doTurnOn:Boolean): Boolean {
        if(Utils.isRooted()) {
            var currentStatueOfScreen = true
            var output: StringBuffer? = null
            try {
                val proc: Process = Runtime.getRuntime().exec("su -0 dumpsys power")
                val stdInput = BufferedReader(InputStreamReader(proc.inputStream))

                var read: Int
                val buffer = CharArray(4096)
                output = StringBuffer()
                while (stdInput.read(buffer).also { read = it } > 0) {
                    output.append(buffer, 0, read)
                }
                stdInput.close()
                val result =
                    output.toString().split("mHoldingDisplaySuspendBlocker=").toTypedArray()
                val resultmHoldingDisplaySuspendBlocker = result[1].split("\n").toTypedArray()
                currentStatueOfScreen = resultmHoldingDisplaySuspendBlocker[0].toBoolean()
                Log.i(
                    "currentStatueOfScreen",
                    "currentStatueOfScreen: " + resultmHoldingDisplaySuspendBlocker[0]
                )

                if (doTurnOn != currentStatueOfScreen) {
                    var r = -1
                    try {
                        val proc: Process = Runtime.getRuntime().exec("su -0 input keyevent 26")
                        r = proc.waitFor()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return r == 0
                } else {
                    Log.i(
                        "currentStatueOfScreen",
                        "currentStatueOfScreen: Screen is on Same state currently"
                    )
                }
                //true->screenON, false->screenOFF
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
            Log.i("HearthDeviceSDK","turnOnOffScreen() device is not rooted")
        }
        return false
    }


    interface CallBackInstallApp {
        fun onSuccess(functionName: String, response: String)
        fun onFailure(functionName: String, exception : Exception)
    }


}