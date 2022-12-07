package com.pmsi.lamaruin.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.CreateRecruiter
import com.pmsi.lamaruin.data.model.CreateStudent
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityRegisterMahasiswaBinding
import com.pmsi.lamaruin.databinding.ActivityRegisterRecruiterBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterRecruiterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterRecruiterBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterRecruiterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnRegister.setOnClickListener(){ registerRecruiter() }
    }

    private fun registerRecruiter() {
        binding.progressBar.isVisible = false

        val username = binding.inputUsername.text.toString()
        val name = binding.inputRecName.text.toString()
        val email = binding.inputRecEmail.text.toString()
        val noHp = binding.inputRecHp.text.toString()
        val password = binding.inputRecPass.text.toString()
        val confirmPass = binding.confirmRecPass.text.toString()
        val kodeAkses = binding.inputKode.text.toString()

        when {
            username.isEmpty() -> {
                binding.inputUsername.error = "Masukkan username"
            }
            name.isEmpty() -> {
                binding.inputRecName.error = "Masukkan nama"
            }
            email.isEmpty() -> {
                binding.inputRecEmail.error = "Masukkan email"
            }
            noHp.isEmpty() -> {
                binding.inputRecHp.error = "Masukkan no handphone"
            }
            password.isEmpty() -> {
                binding.inputRecPass.error = "Masukkan password"
            }
            confirmPass != password -> {
                binding.confirmRecPass.error = "Password tidak sama"
            }
            kodeAkses.isEmpty() -> {
                binding.inputKode.error = "Masukkan kode akses"
            }
            else -> {
                service.searchRecruiter(username, email)
                    .get()
                    .addOnSuccessListener {
                        if (it != null && it.documents.isNotEmpty()) {
                            Toast.makeText(
                                this@RegisterRecruiterActivity,
                                "Username atau email sudah ada",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val recruiters = CreateRecruiter(
                                username,
                                name,
                                email,
                                noHp,
                                password_student = password
                            )

                            service.addRecruiter(recruiters) { id ->
                                LoginPref(this@RegisterRecruiterActivity).setSession(true)
                                binding.progressBar.isVisible = false
//                                Intent(this, MainRecruiterActivity::class.java).apply {
//                                    putExtra("user_id", id)
//                                    startActivity(this)
//                                }
                                Toast.makeText(
                                    this@RegisterRecruiterActivity,
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