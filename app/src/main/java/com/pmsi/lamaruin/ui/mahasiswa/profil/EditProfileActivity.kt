package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityAddEducationBinding
import com.pmsi.lamaruin.databinding.ActivityEditProfileBinding
import com.pmsi.lamaruin.databinding.ActivityRegisterMahasiswaBinding
import com.pmsi.lamaruin.databinding.FragmentProfilBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding

    private var posisi_user: String? = "Marketing"

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = LoginPref(this).getIdMhs().toString()
        setProfile(id)

        binding.apply {
            btnSave.setOnClickListener {
                updateData()
            }
        }
    }

    private fun updateData() {
        binding.progressBar.isVisible = true
        var nama = binding.editNama.text.toString()
        var email = binding.editEmail.text.toString()
        var address = binding.editAddress.text.toString()
        var phone = binding.editPhone.text.toString()
        var desc = binding.editDesc.text.toString()
        val interest = posisi_user.toString()
        var skill = binding.editSkill.text.toString()

        when {
            nama.isEmpty() -> {
                binding.editNama.error = "Field tidak boleh kosong"
            }
            email.isEmpty() -> {
                binding.editEmail.error = "Field tidak boleh kosong"
            }
            address.isEmpty() -> {
                binding.editAddress.error = "Field tidak boleh kosong"
            }
            phone.isEmpty() -> {
                binding.editPhone.error = "Field tidak boleh kosong"
            }
            desc.isEmpty() -> {
                binding.editDesc.error = "Field tidak boleh kosong"
            }
            skill.isEmpty() -> {
                binding.editSkill.error = "Field tidak boleh kosong"
            }
            else -> {
                var id_user = LoginPref(this).getIdMhs().toString()

                service.searchUsersById(id_user)
                    .update("name", nama)
                    .addOnSuccessListener {
                        LoginPref(this).setNamaMhs(nama)
                        Timber.d("Sukses update nama ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update nama ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("email", email)
                    .addOnSuccessListener {
                        Timber.d("Sukses update email ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update email ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("address", address)
                    .addOnSuccessListener {
                        Timber.d("Sukses update address ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update address ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("phone", phone)
                    .addOnSuccessListener {
                        Timber.d("Sukses update phone ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update phone ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("description", desc)
                    .addOnSuccessListener {
                        Timber.d("Sukses update description ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update description ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("interest", interest)
                    .addOnSuccessListener {
                        Timber.d("Sukses update interest ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update interest ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("add_profile", true)
                    .addOnSuccessListener {
                        Timber.d("Sukses update status add profile ke firestore")
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update status add profile ke firestore")
                    }

                service.searchUsersById(id_user)
                    .update("skill", skill)
                    .addOnSuccessListener {
                        Timber.d("Sukses update skill ke firestore")
                        binding.progressBar.isVisible = false
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Timber.tag(ContentValues.TAG).w(e, "Gagal update skill ke firestore")
                    }
            }
        }
    }

    private fun setProfile(id : String){

        service.searchUsersById(id)
            .get()
            .addOnSuccessListener {
                var nama = it.getString("name")
                var email = it.getString("email")
                var foto = it.getString("foto")
                var description = it.getString("description")
                var address = it.getString("address")
                var phone = it.getString("phone")
                var interest = it.getString("interest")
                var skill = it.getString("skill")

                binding.apply {
                    editNama.text = nama.toString().toEditable()
                    editEmail.text = email.toString().toEditable()
                    editDesc.text = description.toString().toEditable()
                    editAddress.text = address.toString().toEditable()
                    editPhone.text = phone.toString().toEditable()
                    editSkill.text = skill.toString().toEditable()
                }

                posisi_user = interest.toString()

                val listPosisi = resources.getStringArray(R.array.posisi_list)
                val adapter = ArrayAdapter(this, R.layout.spinner_item, listPosisi)
                binding.spinner.adapter = adapter

                binding.spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        posisi_user = listPosisi[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }

                binding.ivProfileEdit.load(foto){
                    transformations(CircleCropTransformation())
                }

            }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}