package com.pmsi.lamaruin.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.CreateStudent
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityRegisterMahasiswaBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterMahasiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterMahasiswaBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnRegister.setOnClickListener{ register() }
    }

    private fun register() {
        binding.progressBar.isVisible = false

        val username = binding.inputUsername.text.toString()
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPass.text.toString()
        val confirmPass = binding.confirmPass.text.toString()

        when {
            username.isEmpty() -> {
                binding.inputUsername.error = "Masukkan username"
            }
            name.isEmpty() -> {
                binding.inputName.error = "Masukkan nama"
            }
            email.isEmpty() -> {
                binding.inputEmail.error = "Masukkan email"
            }
            password.isEmpty() -> {
                binding.inputPass.error = "Masukkan password"
            }
            confirmPass.isEmpty() -> {
                binding.confirmPass.error = "Tidak boleh kosong"
            }
            confirmPass != password -> {
                binding.confirmPass.error = "Password tidak sama"
            }
            else -> {
                service.searchUsers(username, email)
                    .get()
                    .addOnSuccessListener {
                        if (it != null && it.documents.isNotEmpty()) {
                            Toast.makeText(
                                this@RegisterMahasiswaActivity,
                                "Akun sudah ada",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else {
                            val users = CreateStudent(
                                username,
                                name,
                                email,
                                password_student = password
                            )

                            service.addStudent(users) { id ->
                                LoginPref(this@RegisterMahasiswaActivity).setSession(true)
                                binding.progressBar.isVisible = false
                                Intent(this, MainMahasiswaActivity::class.java).apply {
                                    putExtra("user_id", id)
                                    startActivity(this)
                                }
                                Toast.makeText(
                                    this@RegisterMahasiswaActivity,
                                    "Berhasil Resister",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }
        }
    }
}