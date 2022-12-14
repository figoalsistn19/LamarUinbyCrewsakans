package com.pmsi.lamaruin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.databinding.FragmentWelcomeBinding
import com.pmsi.lamaruin.ui.mahasiswa.profil.AddEducationActivity

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginPref = LoginPref(requireActivity())
        val isLogin = loginPref.getSession()
        val role = loginPref.getRole()

        if (isLogin && role=="student") {
            val intent = Intent(requireActivity(), MainMahasiswaActivity::class.java)
            startActivity(intent)
        } else if (isLogin && role!="student"){
            val intent = Intent(requireActivity(), MainRecuiterActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_loginStudentActivity)
        )

        binding.button2.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_loginRecruiterActivity)
        )
        binding.button3.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_RegistStudentActivity)
        )
        binding.button4.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_RegistRecruiterActivity)
        )
    }
}