package com.pmsi.lamaruin.login.asstudent

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.CreateRecruiter
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityLoginStudentBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginStudentActivity : AppCompatActivity() {

    @Inject
    lateinit var service: FirestoreService

    private lateinit var binding: ActivityLoginStudentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLoginstud.setOnClickListener{
            login()
        }

    }

    private fun login(){
        binding.progressBar.isVisible = true
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.editEmail.error = "Masukkan email"
            }
            password.isEmpty() -> {
                binding.editPassword.error = "Masukkan password"
            }
            else -> {
                service.loginStudent(email, password)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.d("Listen failed.")
                            binding.progressBar.isVisible = false
                            return@addSnapshotListener
                        }
                        var id_user : String? = null
                        var nama : String? = null
                        for (doc in value!!) {
                            id_user = doc.getString("id_student")
                            nama = doc.getString("name")
                        }
                        if (id_user != null && nama != null) {
                            LoginPref(this).apply {
                                setSession(true)
                                setIdMhs(id_user)
                                setNamaMhs(nama)
                                setRole("student")
                            }
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                this,
                                "Login berhasil",
                                Toast.LENGTH_LONG
                            ).show()
                            val mainStudIntent = Intent(this, MainMahasiswaActivity::class.java)
                            mainStudIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(mainStudIntent)
                        } else {
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                this,
                                "Email atau password salah",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}