package com.pmsi.lamaruin.ui.mahasiswa.profil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityEditProfileBinding
import com.pmsi.lamaruin.databinding.ActivityRegisterMahasiswaBinding
import com.pmsi.lamaruin.databinding.FragmentProfilBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = LoginPref(this).getIdMhs().toString()
        setProfile(id)
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
            }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}