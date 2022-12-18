package com.pmsi.lamaruin.ui.recruiter.profile

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityEditProfileBinding
import com.pmsi.lamaruin.databinding.ActivityEditProfileRecrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class EditProfileRecrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileRecrBinding

    @Inject
    lateinit var service: FirestoreService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileRecrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = LoginPref(this).getIdMhs().toString()
        setProfile(id)

        binding.apply {
            btnSave.setOnClickListener {
                updateData()
            }
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }


    }
    private fun updateData() {
        binding.progressBar.isVisible = true
        var nama = binding.editNama.text.toString()
        var companyName = binding.editCompanyName.text.toString()
        var companyAddress = binding.editCompanyAddress.text.toString()
        var phone = binding.editPhoneNumber.text.toString()
        var companyProfile = binding.editCompanyProfile.text.toString()

        when {
            nama.isEmpty() -> {
                binding.editNama.error = "Field tidak boleh kosong"
            }
            companyName.isEmpty() -> {
                binding.editCompanyName.error = "Field tidak boleh kosong"
            }
            companyAddress.isEmpty() -> {
                binding.editCompanyAddress.error = "Field tidak boleh kosong"
            }
            phone.isEmpty() -> {
                binding.editPhoneNumber.error = "Field tidak boleh kosong"
            }
            companyProfile.isEmpty() -> {
                binding.editCompanyProfile.error = "Field tidak boleh kosong"
            }

            else -> {
                var id_user = LoginPref(this).getIdMhs().toString()

                service.searchRecrById(id_user)
                    .update("name", nama)
                    .addOnSuccessListener {
                        LoginPref(this).setNamaMhs(nama)
                        Timber.d("Sukses update nama ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update nama ke firestore")
                    }

                service.searchRecrById(id_user)
                    .update("company_name", companyName)
                    .addOnSuccessListener {
                        Timber.d("Sukses update Company Name ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update Company Name ke firestore")
                    }

                service.searchRecrById(id_user)
                    .update("company_address", companyAddress)
                    .addOnSuccessListener {
                        Timber.d("Sukses update Company Address ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update address ke firestore")
                    }

                service.searchRecrById(id_user)
                    .update("no_hp", phone)
                    .addOnSuccessListener {
                        Timber.d("Sukses update Phone Number ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update Phone Number ke firestore")
                    }

                service.searchRecrById(id_user)
                    .update("company_profile", companyProfile)
                    .addOnSuccessListener {
                        Timber.d("Sukses update Company Profile ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update Company Profile ke firestore")
                    }


            }
        }
    }

    private fun setProfile(id : String){

        service.searchRecrById(id)
            .get()
            .addOnSuccessListener {
                var nama = it.getString("name")
                var foto = it.getString("foto")
                var idAccount = it.getString("id_recruiter")
                var companyName = it.getString("company_name")
                var companyAddress = it.getString("company_address")
                var companyProfile = it.getString("company_profile")
                var phone = it.getString("no_hp")


                binding.apply {
                    editNama.text = nama.toString().toEditable()
                    editIdAccount.text = idAccount.toString().toEditable()
                    editCompanyName.text = companyName.toString().toEditable()
                    editCompanyProfile.text = companyProfile.toString().toEditable()
                    editPhoneNumber.text = phone.toString().toEditable()
                    editCompanyAddress.text = companyAddress.toString().toEditable()
                }

                binding.ivProfileEdit.load(foto){
                    transformations(CircleCropTransformation())
                }

            }
    }
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}