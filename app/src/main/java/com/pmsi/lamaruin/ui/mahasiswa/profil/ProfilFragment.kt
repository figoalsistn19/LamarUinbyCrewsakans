package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentProfilBinding
import com.pmsi.lamaruin.register.RegisterMahasiswaActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfilFragment : Fragment() {

    private lateinit var binding : FragmentProfilBinding

    private var statAddProfile : Boolean? = null

    @Inject
    lateinit var service: FirestoreService

    private val listEditEduAdapter : ListEditEduAdapter by lazy {
        ListEditEduAdapter()
    }

    private val listEditExpAdapter : ListEditExpAdapter by lazy {
        ListEditExpAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = LoginPref(requireActivity()).getIdMhs().toString()
        setProfil(id)

        binding.apply {
            logout.setOnClickListener { logout() }
            editProfile.setOnClickListener {
                val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun setProfil(id : String){
        binding.progressBar.isVisible = true

        binding.apply {
            rvListEducation.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listEditEduAdapter
                setHasFixedSize(true)
            }

            rvListExperience.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listEditExpAdapter
                setHasFixedSize(true)
            }
        }

        service.searchUsersById(id)
            .get()
            .addOnSuccessListener{
                var nama = it.getString("name")
                var email = it.getString("email")
                var foto = it.getString("foto")
                var description = it.getString("description")
                var address = it.getString("address")
                var phone = it.getString("phone")
                var interest = it.getString("interest")
                var skill = it.getString("skill")

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
                    tvSilakanLengkapi.isVisible = false

                    binding.progressBar.isVisible = false
                }
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
                    binding.rvListEducation.isVisible = false
                } else {
                    binding.tvNothingEdu.isVisible = false
                    binding.rvListEducation.isVisible = true
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
                    binding.rvListExperience.isVisible = false
                } else {
                    binding.tvNothingExp.isVisible = false
                    binding.rvListExperience.isVisible = true
                    listEditExpAdapter.setData(listExp)
                }
            }
    }

    private fun logout(){
        val isLogin = LoginPref(requireActivity())
        isLogin.logout()
        val i = Intent(requireActivity(), RegisterMahasiswaActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        val id = LoginPref(requireActivity()).getIdMhs().toString()
        setProfil(id)
    }
}