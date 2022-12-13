package com.pmsi.lamaruin.ui.recruiter.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pmsi.lamaruin.R
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
    }
}