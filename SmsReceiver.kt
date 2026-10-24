package com.orangtua.kontrol

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private var sedangKedip = false
        private var handlerKedip: Handler? = null
        private var runnableKedip: Runnable? = null

        /**
         * Kedipkan senter selama [durasiMs] milidetik (default 2 menit).
         */
        fun kedipkanSenter(context: Context, durasiMs: Long = 120_000) {
            if (sedangKedip) return
            val camManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = try {
                camManager.cameraIdList.firstOrNull { id ->
                    camManager.getCameraCharacteristics(id)
                        .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
            } catch (e: Exception) { null } ?: return

            sedangKedip = true
            val handler = Handler(Looper.getMainLooper())
            handlerKedip = handler
            var nyala = false
            val interval = 300L // ganti on/off tiap 300ms

            val toggle = object : Runnable {
                override fun run() {
                    try {
                        nyala = !nyala
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            camManager.setTorchMode(cameraId, nyala)
                        }
                    } catch (e: CameraAccessException) {
                        // abaikan
                    }
                    handler.postDelayed(this, interval)
                }
            }
            runnableKedip = toggle
            handler.post(toggle)

            // Hentikan setelah durasi habis
            handler.postDelayed({
                hentikanKedip(camManager, cameraId)
            }, durasiMs)
        }

        private fun hentikanKedip(camManager: CameraManager, cameraId: String) {
            try {
                runnableKedip?.let { handlerKedip?.removeCallbacks(it) }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    camManager.setTorchMode(cameraId, false)
                }
            } catch (e: Exception) { }
            sedangKedip = false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return
        val format = bundle.getString("format")

        val pesanGabungan = StringBuilder()
        for (pdu in pdus) {
            val sms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SmsMessage.createFromPdu(pdu as ByteArray, format)
            } else {
                SmsMessage.createFromPdu(pdu as ByteArray)
            }
            pesanGabungan.append(sms.messageBody)
        }

        val pesan = pesanGabungan.toString().trim()

        // Cocokkan dengan kode rahasia
        when (pesan) {
            MainActivity.KODE_LOCK -> {
                kunciLayar(context)
                abortBroadcast() // sembunyikan SMS dari inbox
            }
            MainActivity.KODE_LAMPU -> {
                kedipkanSenter(context, 120_000) // 2 menit
                abortBroadcast()
            }
        }
    }

    private fun kunciLayar(context: Context) {
        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val compName = ComponentName(context, AdminReceiver::class.java)
            if (dpm.isAdminActive(compName)) {
                dpm.lockNow()
            }
        } catch (e: Exception) { }
    }
}
