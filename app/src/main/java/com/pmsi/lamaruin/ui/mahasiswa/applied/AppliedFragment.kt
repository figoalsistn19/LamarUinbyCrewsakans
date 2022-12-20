package com.pmsi.lamaruin.ui.mahasiswa.applied

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.AppliedJob
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentAppliedBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppliedFragment : Fragment() {

    private lateinit var binding : FragmentAppliedBinding

    private val appliedJobAdapter : AppliedJobAdapter by lazy {
        AppliedJobAdapter()
    }

    @Inject
    lateinit var service: FirestoreService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppliedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvAppliedJobList.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = appliedJobAdapter
                setHasFixedSize(true)
            }
        }

        getAppliedJob()
    }

    private fun getAppliedJob(){
        binding.progressBar.isVisible = true

        var id_student = LoginPref(requireActivity()).getIdMhs()
        service.getAppliedJob(id_student!!)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listJob = ArrayList<AppliedJob>()
                for (doc in value!!) {
                    var id_applied_job = doc.getString("id_applied_job")
                    var status = doc.getString("status")
                    var id_student = doc.getString("id_student")
                    var id_job = doc.getString("id_job")
                    var company_photo = doc.getString("company_photo")
                    var job_title = doc.getString("job_title")
                    var company_name = doc.getString("company_name")

                    var job = AppliedJob(
                        id_applied_job, status, id_student, id_job, company_photo, job_title, company_name
                    )
                    listJob.add(job)
                }
                if (listJob.isEmpty()) {
                    binding.tvNothingAppliedJob.isVisible = true
                    binding.progressBar.isVisible = false
                } else {
                    appliedJobAdapter.setData(listJob)
                    binding.progressBar.isVisible = false
                }
            }
    }
}