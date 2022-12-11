package com.pmsi.lamaruin.ui.mahasiswa.profil

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
        if(id != null){setProfil(id)}
    }


    private fun setProfil(id : String){
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
                        }
                }
//        else if(statAddProfile != null && statAddProfile == true){
//            service.searchUsersById()
//        }
            }
    }

    override fun onResume() {
        super.onResume()
        val id = LoginPref(requireActivity()).getIdMhs()
        if(id != null){setProfil(id)}
    }
}