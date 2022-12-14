package com.pmsi.lamaruin.ui.recruiter.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.WelcomeActivity
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.databinding.FragmentProfilBinding
import com.pmsi.lamaruin.databinding.FragmentProfileRecrBinding


class ProfileRecrFragment : Fragment() {

    private lateinit var binding: FragmentProfileRecrBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileRecrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnInputJob.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_profileRecFragment_to_inputJobActivity)
        )
        binding.button7.setOnClickListener { logout() }
    }

    private fun logout(){
        val isLogin = LoginPref(requireActivity())
        isLogin.logout()
        val i = Intent(requireActivity(), WelcomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }
}