package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.Education
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = LoginPref(this).getIdMhs().toString()
        setProfile(id)

        binding.apply {
            addEducation.setOnClickListener {
                Intent(this@EditProfileActivity,AddEducationActivity::class.java ).apply {
                    startActivity(this)
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
            }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}