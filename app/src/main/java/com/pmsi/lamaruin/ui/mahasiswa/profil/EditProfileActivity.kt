package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
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

    @Inject
    lateinit var service: FirestoreService

    private val listEditEduAdapter : ListEditEduAdapter by lazy {
        ListEditEduAdapter()
    }

    private val listEditExpAdapter : ListEditExpAdapter by lazy {
        ListEditExpAdapter()
    }

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
            addEducation.setOnClickListener {
                Intent(this@EditProfileActivity,AddEducationActivity::class.java ).apply {
                    startActivity(this)
                }
            }
            addExperience.setOnClickListener {
                Intent(this@EditProfileActivity,AddExperienceActivity::class.java ).apply {
                    startActivity(this)
                }
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
        var interest = binding.editInterest.text.toString()
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
            interest.isEmpty() -> {
                binding.editInterest.error = "Field tidak boleh kosong"
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
        binding.apply {
            rvListEditEducation.apply {
                layoutManager = LinearLayoutManager(this@EditProfileActivity)
                adapter = listEditEduAdapter
                setHasFixedSize(true)
            }

            rvListEditExperience.apply {
                layoutManager = LinearLayoutManager(this@EditProfileActivity)
                adapter = listEditExpAdapter
                setHasFixedSize(true)
            }
        }

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
                var url_cv = it.getString("url_cv")
                var nama_cv = it.getString("nama_cv")
                var id_cv = it.getString("id_cv")

                binding.apply {
                    editNama.text = nama.toString().toEditable()
                    editEmail.text = email.toString().toEditable()
                    editDesc.text = description.toString().toEditable()
                    editAddress.text = address.toString().toEditable()
                    editPhone.text = phone.toString().toEditable()
                    editInterest.text = interest.toString().toEditable()
                    editSkill.text = skill.toString().toEditable()
                    fileName.text = nama_cv.toString().toEditable()
                }

                // get list education
                service.getEdu(id)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                            return@addSnapshotListener
                        }
                        var listEdu = ArrayList<Education>()
                        for (doc in value!!) {
                            var jurusan = doc.getString("field_of_study")
                            var degree = doc.getString("degree")
                            var univ = doc.getString("school")
                            var id_edu = doc.getString("edu")
                            var start_year = doc.getString("education_start_date")
                            var end_year = doc.getString("education_end_date")
                            var edu = Education(jurusan, degree, univ,start_year, end_year, id_edu)
                            listEdu.add(edu)
                        }
                        if (listEdu.isEmpty()) {
                            binding.tvNothingEdu.isVisible = true
                            binding.rvListEditEducation.isVisible = false
                        } else {
                            binding.tvNothingEdu.isVisible = false
                            binding.rvListEditEducation.isVisible = true
                            listEditEduAdapter.setData(listEdu)
                        }
                    }

                // get list experience
                service.getExp(id)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                            return@addSnapshotListener
                        }
                        var listExp = ArrayList<Experience>()
                        for (doc in value!!) {
                            var title = doc.getString("title")
                            var role = doc.getString("role")
                            var desc = doc.getString("experience_desc")
                            var exp_start_date = doc.getString("experience_start_date")
                            var exp_end_date = doc.getString("experience_end_date")
                            var exp = Experience(title, role, desc, exp_start_date, exp_end_date)
                            listExp.add(exp)
                        }
                        if (listExp.isEmpty()) {
                            binding.tvNothingExp.isVisible = true
                            binding.rvListEditExperience.isVisible = false
                        } else {
                            binding.tvNothingExp.isVisible = false
                            binding.rvListEditExperience.isVisible = true
                            listEditExpAdapter.setData(listExp)
                        }
                    }
            }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}