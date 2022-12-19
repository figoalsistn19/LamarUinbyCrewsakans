package com.pmsi.lamaruin.ui.mahasiswa.listJob

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentListJobBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ListJobFragment : Fragment() {

    private lateinit var binding : FragmentListJobBinding

    private val listJobAdapter : ListJobAdapter by lazy {
        ListJobAdapter{
            Intent(requireActivity(), DetailJobActivity::class.java).apply {
                putExtra("id_job", it.id_job)
                putExtra("id_recruiter", it.id_recruiter)
                startActivity(this)
            }
        }
    }

    @Inject
    lateinit var service: FirestoreService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListJobBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id_user = LoginPref(requireActivity()).getIdMhs().toString()
        var interest: String? = ""

        // get interest
        service.searchUsersById(id_user)
            .get()
            .addOnSuccessListener {
                interest = it.getString("interest")

                // get job
                if (interest == ""){
                    getJobWithoutInterest()
                } else if (interest != ""){
                    getJobWithInterest(interest!!)
                }
            }

        var nama_user = LoginPref(requireActivity()).getNamaMhs()
        if(nama_user != null){
            var first_name = getFirstName(nama_user)
            binding.tvHello.text = "Hello, $first_name"
        } else{
            binding.tvHello.isVisible = false
        }

        binding.apply {
            rvOtherJobList.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listJobAdapter
                setHasFixedSize(true)
            }
        }
    }

    fun getFirstName(name: String): String {
        val names = name.trim().split(Regex("\\s+"))
        return names.first()
//        return names.firstOrNull() to names.lastOrNull()
    }

    private fun getJobWithoutInterest(){
        binding.progressBar.isVisible = true
        binding.titleRecommend.isVisible = false
        binding.rvRecomJobList.isVisible = false
        binding.titleOtherJob.isVisible = false

        service.getJob()
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = "12-12-2022"

                    var foto : String? = ""
                    var company_name : String? = ""
                    var company_city : String? = ""

//                    get company profile
                    if(id_recruiter != null){
                        service.getRecruiterById(id_recruiter)
                            .get()
                            .addOnSuccessListener {
                                foto = it.getString("foto")
                                company_name = it.getString("company_name")
                                company_city = it.getString("company_address")

                                var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggat)
                                listJob.add(job)

                                if (listJob.isEmpty()) {
                                    binding.tvNothingJob.isVisible = true
                                    binding.rvOtherJobList.isVisible = false
                                    binding.progressBar.isVisible = false
                                } else {
                                    listJobAdapter.setData(listJob)
                                    binding.progressBar.isVisible = false
                                }
                            }
                    }

                    // buat sementara get company profile
//                    foto = "https://karier.uinjkt.ac.id/public/main/1616805063_logo_portrait.png"
//                    company_name = "Pusat Karier UIN Jakarta"
//                    company_city = "Tanggerang Selatan"
                }
            }
    }

    private fun getJobWithInterest(interest : String){
        binding.progressBar.isVisible = true
        getRecommendJob(interest)
        getOtherJobWithInterest(interest)
    }

    private fun getRecommendJob(interest: String) {
        service.getRecommendJob(interest)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
                    return@addSnapshotListener
                }
                var listRecomJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = "12-12-2022"

                    var foto : String? = ""
                    var company_name : String? = ""
                    var company_city : String? = ""

//                    get company profile
                    if(id_recruiter != null){
                        service.getRecruiterById(id_recruiter)
                            .get()
                            .addOnSuccessListener {
                                foto = it.getString("foto")
                                company_name = it.getString("company_name")
                                company_city = it.getString("company_address")

                                var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggat)
                                listRecomJob.add(job)

                                if (listRecomJob.isEmpty()) {
                                    binding.titleRecommend.isVisible = false
                                    binding.titleOtherJob.isVisible = false
                                    binding.rvRecomJobList.isVisible = false
                                    binding.progressBar.isVisible = false
                                } else {
                                    listJobAdapter.setData(listRecomJob)
                                    binding.progressBar.isVisible = false
                                }
                            }
                    }
                    var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggat)
                    listRecomJob.add(job)

                    // buat sementara get company profile
//                    foto = "https://karier.uinjkt.ac.id/public/main/1616805063_logo_portrait.png"
//                    company_name = "Pusat Karier UIN Jakarta"
//                    company_city = "Tanggerang Selatan"
                }
                if (listRecomJob.isEmpty()) {
                    binding.titleRecommend.isVisible = false
                    binding.titleOtherJob.isVisible = false
                    binding.rvRecomJobList.isVisible = false
                    binding.progressBar.isVisible = false
                } else {
                    listJobAdapter.setData(listRecomJob)
                    binding.progressBar.isVisible = false
                }
                binding.progressBar.isVisible = false
            }
    }

    private fun getOtherJobWithInterest(interest : String){
        // get other job with interest
        service.getJobWithInterest(interest)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
                    return@addSnapshotListener
                }
                var listJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = "12-12-2022"

                    var foto : String? = ""
                    var company_name : String? = ""
                    var company_city : String? = ""
                    //get company profile
                    if(id_recruiter != null){
                        service.getRecruiterById(id_recruiter)
                            .get()
                            .addOnSuccessListener {
                                foto = it.getString("foto")
                                company_name = it.getString("company_name")
                                company_city = it.getString("company_address")

                                var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggat)
                                listJob.add(job)

                                if (listJob.isEmpty()) {
                                    binding.tvNothingJob.isVisible = true
                                    binding.titleOtherJob.isVisible = true
                                    binding.rvOtherJobList.isVisible = false
                                } else {
                                    listJobAdapter.setData(listJob)
                                }
                            }
                    }

                    // buat sementara get company profile
//                    foto = "https://karier.uinjkt.ac.id/public/main/1616805063_logo_portrait.png"
//                    company_name = "Pusat Karier UIN Jakarta"
//                    company_city = "Tanggerang Selatan"
                }
            }
    }
}