package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentProfilBinding
import com.pmsi.lamaruin.register.RegisterMahasiswaActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilFragment : Fragment() {

    private lateinit var binding : FragmentProfilBinding

    private var statAddProfile : Boolean? = null

    @Inject
    lateinit var service: FirestoreService

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

        service.searchUsersById(id)
            .get()
            .addOnSuccessListener {
                statAddProfile = it.getBoolean("addProfile")

                if (statAddProfile != null && statAddProfile == false){
                    service.searchUsersById(id)
                        .get()
                        .addOnSuccessListener {
                            var nama = it.getString("name")
                            var email = it.getString("email")
                            var foto = it.getString("foto")

                            binding.tvNamaMahasiswa.text = nama
                            binding.tvEmailMhs.text = email
                            binding.ivProfile.load(foto){
                                transformations(CircleCropTransformation())
                            }
                            binding.tvSilakanLengkapi.isVisible = true
                            binding.linearDetailProfile.isVisible = false

                            binding.progressBar.isVisible = false
                        }
                }
                else if(statAddProfile != null && statAddProfile == true){
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