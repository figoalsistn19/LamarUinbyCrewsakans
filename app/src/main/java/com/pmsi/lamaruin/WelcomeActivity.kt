package com.pmsi.lamaruin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.pmsi.lamaruin.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}