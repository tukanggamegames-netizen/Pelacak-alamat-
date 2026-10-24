package com.orangtua.kontrol

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dpm: DevicePolicyManager
    private lateinit var compName: ComponentName

    companion object {
        // KODE RAHASIA — ganti dengan kode kamu sendiri!
        // Kirim SMS isi:  KODE_LOCK   -> kunci layar anak
        //                 KODE_LAMPU  -> senter berkedip 2 menit
        var KODE_LOCK = "ORTU_LOCK123"
        var KODE_LAMPU = "ORTU_LAMPU123"
        const val REQUEST_ENABLE_ADMIN = 1001
        const val REQUEST_SMS_PERM = 1002
        const val REQUEST_CAMERA_PERM = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, AdminReceiver::class.java)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val etKodeLock = findViewById<EditText>(R.id.etKodeLock)
        val etKodeLampu = findViewById<EditText>(R.id.etKodeLampu)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnAktifkanAdmin = findViewById<Button>(R.id.btnAktifkanAdmin)
        val btnTestLock = findViewById<Button>(R.id.btnTestLock)
        val btnTestLampu = findViewById<Button>(R.id.btnTestLampu)

        etKodeLock.setText(KODE_LOCK)
        etKodeLampu.setText(KODE_LAMPU)

        btnSimpan.setOnClickListener {
            KODE_LOCK = etKodeLock.text.toString().trim()
            KODE_LAMPU = etKodeLampu.text.toString().trim()
            Toast.makeText(this, "Kode tersimpan", Toast.LENGTH_SHORT).show()
        }

        btnAktifkanAdmin.setOnClickListener {
            if (!dpm.isAdminActive(compName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Diperlukan agar orang tua bisa mengunci layar dari jarak jauh.")
                }
                startActivityForResult(intent, REQUEST_ENABLE_ADMIN)
            } else {
                Toast.makeText(this, "Device Admin sudah aktif", Toast.LENGTH_SHORT).show()
            }
        }

        btnTestLock.setOnClickListener {
            if (dpm.isAdminActive(compName)) {
                dpm.lockNow()
            } else {
                Toast.makeText(this, "Aktifkan Device Admin dulu", Toast.LENGTH_SHORT).show()
            }
        }

        btnTestLampu.setOnClickListener {
            SmsReceiver.kedipkanSenter(this, 120_000) // 2 menit
            Toast.makeText(this, "Senter berkedip 2 menit", Toast.LENGTH_SHORT).show()
        }

        // Minta izin SMS & Kamera
        mintaIzin()

        // Cek status
        Handler(Looper.getMainLooper()).postDelayed({
            val admin = if (dpm.isAdminActive(compName)) "AKTIF" else "BELUM AKTIF"
            tvStatus.text = "Device Admin: $admin\n" +
                    "Kode Lock SMS: $KODE_LOCK\n" +
                    "Kode Lampu SMS: $KODE_LAMPU"
        }, 500)
    }

    private fun mintaIzin() {
        val perms = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) perms.add(Manifest.permission.RECEIVE_SMS)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) perms.add(Manifest.permission.READ_SMS)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) perms.add(Manifest.permission.CAMERA)
        if (perms.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, perms.toTypedArray(), REQUEST_SMS_PERM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_ADMIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device Admin aktif!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
