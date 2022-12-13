package com.pmsi.lamaruin.ui.mahasiswa.profil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pmsi.lamaruin.databinding.ActivityDetailCvBinding
import timber.log.Timber

class DetailCvActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailCvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val urlCv = intent.getStringExtra("data")

        binding.apply {
            val settings = webview.settings
            settings.javaScriptEnabled = true
            settings.setSupportZoom(false)

            urlCv?.let {
                val url = "https://drive.google.com/viewerng/viewer?embedded=true&url=$it"
                Timber.e(url)
                webview.loadUrl(it)
            }
        }
    }
}