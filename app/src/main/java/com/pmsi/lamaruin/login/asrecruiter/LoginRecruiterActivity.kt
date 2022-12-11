package com.pmsi.lamaruin.login.asrecruiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pmsi.lamaruin.MainRecuiterActivity
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.databinding.ActivityLoginRecruiterBinding

class LoginRecruiterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRecruiterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRecruiterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoginrec.setOnClickListener {
            val intent = Intent(this@LoginRecruiterActivity, MainRecuiterActivity::class.java)
            startActivity(intent)
        }
    }
}