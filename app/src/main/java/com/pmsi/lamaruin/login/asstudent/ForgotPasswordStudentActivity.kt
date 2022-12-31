package com.pmsi.lamaruin.login.asstudent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.WelcomeActivity
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityForgotPasswordStudentBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordStudentBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        binding.btnSignup.setOnClickListener {
            val intent = Intent(this@ForgotPasswordStudentActivity, WelcomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnChangePw.setOnClickListener {
            checkEmailHp()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun checkEmailHp(){
        var email = binding.editText.text.toString()
        var noHp = binding.editText2.text.toString()

        when {
            email.isEmpty() -> {
                binding.editText.error = "Email tidak boleh kosong"
            }
            noHp.isEmpty() -> {
                binding.editText2.error = "No Handphone tidak boleh kosong"
            }
            else -> {
                service.checkEmailHpStudent(email,noHp)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.d("Listen failed.")
                            return@addSnapshotListener
                        }
                        var id_user : String? = null
                        for (doc in value!!) {
                            id_user = doc.getString("id_student")
                        }
                        if (id_user != null) {
                            Intent(this, NewPasswordStudentActivity::class.java).apply {
                                putExtra("id_user", id_user)
                                startActivity(this)
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Email atau No HP salah",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}