package com.pmsi.lamaruin.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.MainRecuiterActivity
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.CreateRecruiter
import com.pmsi.lamaruin.data.model.CreateStudent
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityRegisterMahasiswaBinding
import com.pmsi.lamaruin.databinding.ActivityRegisterRecruiterBinding
import com.pmsi.lamaruin.login.asrecruiter.LoginRecruiterActivity
import com.pmsi.lamaruin.login.asstudent.LoginStudentActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

        binding.btnLogin.setOnClickListener {
            Intent(this, LoginRecruiterActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun registerRecruiter() {
        binding.progressBar.isVisible = true

        val name = binding.inputRecName.text.toString()
        val email = binding.inputRecEmail.text.toString()
        val noHp = binding.inputRecHp.text.toString()
        val password = binding.inputRecPass.text.toString()
        val confirmPass = binding.confirmRecPass.text.toString()
        val kodeAkses = binding.inputKode.text.toString()

        when {
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
                service.searchRecruiter(email)
                    .get()
                    .addOnSuccessListener {
                        if (it != null && it.documents.isNotEmpty()) {
                            Toast.makeText(
                                this@RegisterRecruiterActivity,
                                "Email sudah ada",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            service.searchKodeAkses(kodeAkses)
                                .get()
                                .addOnSuccessListener {
                                    if (it != null && it.documents.isNotEmpty()) {
                                        val id_kode = it.documents[0].id

                                        // ubah status penggunaan kode akses
                                        service.updateStatusKode(id_kode)

                                        val recruiters = CreateRecruiter(
                                            name,
                                            email,
                                            noHp,
                                            password_recruiter = password
                                        )

                                        // input ke collection recruiter
                                        service.addRecruiter(recruiters) { id ->
                                            LoginPref(this@RegisterRecruiterActivity).apply {
                                                setSession(true)
                                                setRole("recruiter")
                                                setNamaMhs(name)
                                                setIdMhs(id)
                                            }
                                            binding.progressBar.isVisible = false

                                            val i = Intent(this, MainRecuiterActivity::class.java)
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(i)

                                            Toast.makeText(
                                                this@RegisterRecruiterActivity,
                                                "Berhasil Register",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        binding.progressBar.isVisible = false
                                        Toast.makeText(
                                            this@RegisterRecruiterActivity,
                                            "Kode Akses Salah",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    }
            }
        }
    }
}