package com.pmsi.lamaruin.ui.recruiter.activelisting.detail.detailpelamar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import coil.transform.CircleCropTransformation
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.model.ItemListPelamar
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityDetailApplicantBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailApplicantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailApplicantBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailApplicantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        var id = intent.getStringExtra("id_pelamar")
        setProfile(id!!)

    }

    private fun setProfile(id: String){
        service.searchUsersById(id)
            .get()
            .addOnSuccessListener {
                var pelamar = ArrayList<ItemListPelamar>()
                var id_student = it.getString("id_student")
                var nama = it.getString("name")
                var email = it.getString("email")
                var foto = it.getString("foto")
                var description = it.getString("description")
                var address = it.getString("address")
                var phone = it.getString("phone")
                var interest = it.getString("interest")
                var skill = it.getString("skill")
                var nama_cv = it.getString("nama_cv")
                var link_cv = it.getString("link_cv")

                binding.ivProfile.load(foto){
                    transformations(CircleCropTransformation())
                }

                binding.apply{
                    tvNamaMahasiswa.text = nama
                    tvEmailMhs.text = email
                    tvAddressUser.text = address
                    tvDescUser.text = description
                    tvPhoneUser.text = phone
                    tvInterestUser.text = interest
                    tvSkillUser.text = skill
                    tvNamaCV.text = nama_cv
                }
                if(description == ""){ binding.tvDescUser.text = "Not yet Added" }
                if(address == ""){ binding.tvAddressUser.text = "Not yet Added" }
                if(phone == ""){ binding.tvPhoneUser.text = "Not yet Added" }
                if(interest == ""){ binding.tvInterestUser.text = "Not yet Added" }
                if(skill == ""){ binding.tvSkillUser.text = "Not yet Added" }
                if(nama_cv == ""){ binding.tvNamaCV.text = "No file chosen" }
            }
    }
    override fun onResume() {
        super.onResume()
        val id = intent.getStringExtra("id_pelamar")
        setProfile(id!!)
    }
}