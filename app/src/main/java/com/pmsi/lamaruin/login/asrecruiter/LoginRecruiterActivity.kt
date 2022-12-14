package com.pmsi.lamaruin.login.asrecruiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.MainRecuiterActivity
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityLoginRecruiterBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginRecruiterActivity : AppCompatActivity() {

    @Inject
    lateinit var service: FirestoreService

    private lateinit var binding: ActivityLoginRecruiterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRecruiterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLoginrec.setOnClickListener {
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
                service.loginRecruiter(email, password)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.d("Listen failed.")
                            binding.progressBar.isVisible = false
                            return@addSnapshotListener
                        }
                        var id_user : String? = null
                        var nama : String? = null
                        for (doc in value!!) {
                            id_user = doc.getString("id_recruiter")
                            nama = doc.getString("name")
                        }
                        if (id_user != null && nama != null) {
                            LoginPref(this).apply {
                                setSession(true)
                                setIdMhs(id_user)
                                setNamaMhs(nama)
                                setRole("recruiter")
                            }
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                this,
                                "Login berhasil",
                                Toast.LENGTH_LONG
                            ).show()
                            val i = Intent(this, MainRecuiterActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
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