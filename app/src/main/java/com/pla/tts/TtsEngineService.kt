package com.pla.tts

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.File

class TtsEngineService : Service() {

    companion object {
        private const val TAG = "PlaTtsEngine"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val text = intent?.getStringExtra("TEXT_DATA") ?: ""
        val isOffline = intent?.getBooleanExtra("IS_OFFLINE", false) ?: false

        if (isOffline) {
            synthesizeWithCustomModel(text)
        } else {
            synthesizeOnlineApi(text)
        }

        return START_NOT_STICKY
    }

    private fun synthesizeWithCustomModel(text: String) {
        val modelFile = File(filesDir, "pla_custom_voice.bin")
        
        if (modelFile.exists()) {
            Log.d(TAG, "Menjalankan mesin TTS Offline dengan model kustom eksternal untuk teks: $text")
            // Di sinilah tempat integrasi engine neural kustom (cth: ONNX Runtime / Piper C++ binary)
            // Memproses file model .bin/.onnx langsung di memori aplikasi tanpa menyentuh TTS bawaan HP.
        } else {
            Log.e(TAG, "Gagal: File model suara kustom offline belum diunduh.")
        }
        stopSelf()
    }

    private fun synthesizeOnlineApi(text: String) {
        Log.d(TAG, "Menjalankan mesin TTS Online via server API kustom untuk teks: $text")
        // Memanggil API server eksternal untuk menghasilkan suara natural.
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
