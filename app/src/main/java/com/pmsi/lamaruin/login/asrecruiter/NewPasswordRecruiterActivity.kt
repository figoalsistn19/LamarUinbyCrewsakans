package com.pmsi.lamaruin.login.asrecruiter

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityNewPasswordRecruiterBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NewPasswordRecruiterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordRecruiterBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordRecruiterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val id_recruiter = intent.getStringExtra("id_recruiter")
            changePw(id_recruiter!!)
        }
    }

    private fun changePw(id_recruiter : String){
        var password = binding.editText.text.toString()
        var confirm_password = binding.editText2.text.toString()

        when{
            password.isEmpty() -> {
                binding.editText.error = "Password tidak boleh kosong"
            }
            confirm_password != password ->{
                binding.editText2.error = "Password tidak sama"
            }
            else -> {
                service.updatePasswordRecruiter(id_recruiter, password)
                    .addOnSuccessListener {
                        Timber.d("Sukses update password ke firestore")
                        Toast.makeText(
                            this,
                            "Ubah password sukses, silakan login",
                            Toast.LENGTH_LONG
                        ).show()
                        val i = Intent(this, LoginRecruiterActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update password ke firestore")
                        Toast.makeText(
                            this,
                            "Maaf terjadi kesalahan, silakan coba lagi",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}