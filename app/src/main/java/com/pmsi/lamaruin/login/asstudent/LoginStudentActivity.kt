package com.pmsi.lamaruin.login.asstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.databinding.ActivityLoginStudentBinding

class LoginStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginStudentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoginstud.setOnClickListener{
            val mainStudIntent = Intent(this, MainMahasiswaActivity::class.java)
            startActivity(mainStudIntent)
        }

    }
}