package com.pla.tts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var rgMode: RadioGroup
    private lateinit var rbOnline: RadioButton
    private lateinit var rbOffline: RadioButton
    private lateinit var layoutDownload: LinearLayout
    private lateinit var btnDownloadModel: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var etInputText: EditText
    private lateinit var btnSpeak: Button
    private lateinit var tvStatus: TextView

    private var isModelDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rgMode = findViewById(R.id.rgMode)
        rbOnline = findViewById(R.id.rbOnline)
        rbOffline = findViewById(R.id.rbOffline)
        layoutDownload = findViewById(R.id.layoutDownload)
        btnDownloadModel = findViewById(R.id.btnDownloadModel)
        progressBar = findViewById(R.id.progressBar)
        etInputText = findViewById(R.id.etInputText)
        btnSpeak = findViewById(R.id.btnSpeak)
        tvStatus = findViewById(R.id.tvStatus)

        checkOfflineModelExists()

        rgMode.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbOffline) {
                layoutDownload.visibility = View.VISIBLE
                if (!isModelDownloaded) {
                    tvStatus.text = "Status: Model offline belum diunduh. Silakan unduh terlebih dahulu."
                } else {
                    tvStatus.text = "Status: Model offline siap digunakan."
                }
            } else {
                layoutDownload.visibility = View.GONE
                tvStatus.text = "Status: Mode Online aktif."
            }
        }

        btnDownloadModel.setOnClickListener {
            downloadCustomVoiceModel()
        }

        btnSpeak.setOnClickListener {
            val text = etInputText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Masukkan teks terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isOffline = rbOffline.isChecked
            if (isOffline && !isModelDownloaded) {
                Toast.makeText(this, "Harap unduh model suara kustom offline terlebih dahulu!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            startTtsService(text, isOffline)
        }
    }

    private fun checkOfflineModelExists() {
        val modelFile = File(filesDir, "pla_custom_voice.bin")
        if (modelFile.exists()) {
            isModelDownloaded = true
            tvStatus.text = "Status: Model offline tersedia."
        }
    }

    private fun downloadCustomVoiceModel() {
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        btnDownloadModel.isEnabled = false
        tvStatus.text = "Status: Mengunduh model suara kustom..."

        Thread {
            for (i in 1..10) {
                Thread.sleep(200)
                runOnUiThread {
                    progressBar.progress = i * 10
                }
            }
            runOnUiThread {
                val modelFile = File(filesDir, "pla_custom_voice.bin")
                modelFile.writeText("PLA_TTS_CUSTOM_MODEL_BINARY_DATA")
                isModelDownloaded = true
                progressBar.visibility = View.GONE
                btnDownloadModel.isEnabled = true
                tvStatus.text = "Status: Model suara kustom berhasil diunduh dan disimpan."
                Toast.makeText(this, "Model offline siap!", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun startTtsService(text: String, isOffline: Boolean) {
        val intent = Intent(this, TtsEngineService::class.java).apply {
            putExtra("TEXT_DATA", text)
            putExtra("IS_OFFLINE", isOffline)
        }
        startService(intent)
        val modeStr = if (isOffline) "Offline (Custom Model)" else "Online"
        tvStatus.text = "Status: Memproses sintesis suara ($modeStr)..."
    }
}
