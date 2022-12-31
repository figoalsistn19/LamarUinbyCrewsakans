package com.pmsi.lamaruin.login.asrecruiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.WelcomeActivity
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityForgotPasswordRecruiterBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordRecruiterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordRecruiterBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordRecruiterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
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
                service.checkEmailHpRecruiter(email,noHp)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.d("Listen failed.")
                            return@addSnapshotListener
                        }
                        var id_recruiter : String? = null
                        for (doc in value!!) {
                            id_recruiter = doc.getString("id_recruiter")
                        }
                        if (id_recruiter != null) {
                            Intent(this, NewPasswordRecruiterActivity::class.java).apply {
                                putExtra("id_recruiter", id_recruiter)
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