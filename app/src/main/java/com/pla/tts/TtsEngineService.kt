package com.pla.tts

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.File

class TtsEngineService : Service() {

    companion object {
        private const val TAG = "TtsEngineService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val text = intent?.getStringExtra("TEXT_DATA") ?: ""
        val isOffline = intent?.getBooleanExtra("IS_OFFLINE", false) ?: false

        if (isOffline) {
            processOfflineSpeech(text)
        } else {
            processOnlineSpeech(text)
        }

        return START_NOT_STICKY
    }

    private fun processOfflineSpeech(text: String) {
        val modelFile = File(filesDir, "pla_custom_voice.bin")
        if (modelFile.exists()) {
            Log.d(TAG, "Memproses Offline TTS menggunakan model eksternal untuk teks: $text")
        } else {
            Log.e(TAG, "File model offline tidak ditemukan.")
        }
        stopSelf()
    }

    private fun processOnlineSpeech(text: String) {
        Log.d(TAG, "Memproses Online TTS untuk teks: $text")
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
