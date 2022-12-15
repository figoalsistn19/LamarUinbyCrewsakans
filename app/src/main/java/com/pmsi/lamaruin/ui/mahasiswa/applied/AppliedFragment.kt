package com.pmsi.lamaruin.ui.mahasiswa.applied

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentListJobBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.ListJobAdapter
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppliedFragment : Fragment() {

    private lateinit var binding : FragmentListJobBinding

    private val appliedJobAdapter : AppliedJobAdapter by lazy {
        AppliedJobAdapter()
    }

    @Inject
    lateinit var service: FirestoreService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applied, container, false)
    }
}