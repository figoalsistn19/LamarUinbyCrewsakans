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
import com.pmsi.lamaruin.login.asstudent.LoginStudentActivity
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
        binding.btnLogin.setOnClickListener {
            Intent(this, LoginStudentActivity::class.java).apply {
                startActivity(this)
            }
        }

    }

    private fun register() {
        binding.progressBar.isVisible = true

        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPass.text.toString()
        val confirmPass = binding.confirmPass.text.toString()

        when {
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
                service.searchUsers(email)
                    .get()
                    .addOnSuccessListener {
                        if (it != null && it.documents.isNotEmpty()) {
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                this@RegisterMahasiswaActivity,
                                "Email sudah terdaftar",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else {
                            val users = CreateStudent(
                                name,
                                email,
                                foto = "https://i.pinimg.com/474x/86/ea/e3/86eae3d8abc2362ad6262916cb950640.jpg",
                                password_student = password,
                                add_profile = false
                            )

                            service.addStudent(users) { id ->
                                LoginPref(this@RegisterMahasiswaActivity).apply {
                                    setSession(true)
                                    setIdMhs(id)
                                    setNamaMhs(name)
                                    setRole("student")
                                }
                                LoginPref(this@RegisterMahasiswaActivity)
                                binding.progressBar.isVisible = false

                                val i = Intent(this, MainMahasiswaActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)

                                Toast.makeText(
                                    this@RegisterMahasiswaActivity,
                                    "Berhasil Register",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }
        }
    }
}