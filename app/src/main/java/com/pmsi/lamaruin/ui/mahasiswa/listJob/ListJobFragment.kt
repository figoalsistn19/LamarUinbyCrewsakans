package com.pmsi.lamaruin.ui.mahasiswa.listJob

import android.content.Intent
import android.os.Bundle
import java.util.Timer
import kotlin.concurrent.schedule
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentListJobBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import com.pmsi.lamaruin.ui.mahasiswa.listJob.search.ListSearchJobAdapter
import com.pmsi.lamaruin.ui.mahasiswa.listJob.search.SearchJobActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ListJobFragment : Fragment() {

    private lateinit var binding: FragmentListJobBinding

    private val listJobAdapter: ListJobAdapter by lazy {
        ListJobAdapter {
            Intent(requireActivity(), DetailJobActivity::class.java).apply {
                putExtra("id_job", it.id_job)
                putExtra("id_recruiter", it.id_recruiter)
                startActivity(this)
            }
        }
    }

    private val listRecomJobAdapter: ListRecomJobAdapter by lazy {
        ListRecomJobAdapter {
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
        setProfile (id_user)

        binding.apply {
            rvOtherJobList.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listJobAdapter
                setHasFixedSize(true)
            }
        }

        binding.apply {
            rvRecomJobList.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listRecomJobAdapter
                setHasFixedSize(true)
            }
        }

        binding.searchButton.setOnClickListener {
            var text = binding.searchBox.text.toString()
            Intent(requireActivity(), SearchJobActivity::class.java).apply {
                putExtra("text_search", text)
                startActivity(this)
            }
        }
    }

    private fun setProfile(id_user: String){
        var interest: String? = ""

        service.searchUsersById(id_user)
            .get()
            .addOnSuccessListener {
                interest = it.getString("interest")

                // get job
                if (interest == ""){
                    getJobWithoutInterest()
                } else if (interest != ""){
                    getJobWithInterest(interest!!)
//                    getJobByDate(interest!!)
                }
            }

        var nama_user = LoginPref(requireActivity()).getNamaMhs()
        if(nama_user != null){
            var first_name = getFirstName(nama_user)
            binding.tvHello.text = "Hello, $first_name!"
        } else{
            binding.tvHello.isVisible = false
        }
    }
    //oke

    fun getFirstName(name: String): String {
        val names = name.trim().split(Regex("\\s+"))
        return names.first()
    }

    fun getDateToday(): Long {
        var dueDateMillis: Long = System.currentTimeMillis()
        return dueDateMillis
    }

    fun changeDate(date: Long) : String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = dateFormat.format(date)
        return date
    }

    private fun getJobByDate(interest: String){
        binding.progressBar.isVisible = true
        var date = getDateToday()
        service.getJobByDate(date)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listOtherJob = ArrayList<ItemJob>()
                var listRecJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = doc.getLong("job_deadline")
                    var tenggatString = changeDate(tenggat!!)

                    var foto : String? = ""
                    var company_name : String? = ""
                    var company_city : String? = ""

//                    get company profile
                    if(id_recruiter != null) {
                        service.getRecruiterById(id_recruiter)
                            .get()
                            .addOnSuccessListener {
                                foto = it.getString("foto")
                                company_name = it.getString("company_name")
                                company_city = it.getString("company_address")

                                var job = ItemJob(
                                    id_job,
                                    id_recruiter,
                                    name,
                                    cat,
                                    company_name,
                                    company_city,
                                    foto,
                                    tenggatString
                                )

                                if (cat == interest) {
                                    listRecJob.add(job)
                                } else {
                                    listOtherJob.add(job)
                                }
                            }
                    }
                }
                // Delay of 5 sec
                Timer().schedule(5000){
                    //calling a function
                    pengecekan(listOtherJob, listRecJob)
                }
            }
    }

    private fun pengecekan(listOtherJob: ArrayList<ItemJob>, listRecJob: ArrayList<ItemJob>){
        if (listRecJob.isEmpty() && listOtherJob.isNotEmpty()){
            binding.titleRecommend.isVisible = false
            binding.titleOtherJob.isVisible = false
            binding.rvRecomJobList.isVisible = false
            binding.rvOtherJobList.isVisible = true
            listJobAdapter.setData(listOtherJob)
            binding.progressBar.isVisible = false
            binding.tvNothingJob.isVisible = false
        } else if (listOtherJob.isEmpty() && listRecJob.isNotEmpty()){
            binding.titleRecommend.isVisible = false
            binding.titleOtherJob.isVisible = false
            binding.rvOtherJobList.isVisible = false
            binding.rvRecomJobList.isVisible = true
            listRecomJobAdapter.setData(listRecJob)
            binding.progressBar.isVisible = false
            binding.tvNothingJob.isVisible = false
        } else if (listOtherJob.isEmpty() && listRecJob.isEmpty()){
            binding.titleRecommend.isVisible = false
            binding.titleOtherJob.isVisible = false
            binding.rvOtherJobList.isVisible = false
            binding.rvRecomJobList.isVisible = false
            binding.tvNothingJob.isVisible = true
            binding.progressBar.isVisible = false
        }
    }

    private fun getJobWithoutInterest(){
        binding.progressBar.isVisible = true
        binding.titleRecommend.isVisible = false
        binding.rvRecomJobList.isVisible = false
        binding.titleOtherJob.isVisible = false

        var date = getDateToday()
        service.getJob()
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = doc.getLong("job_deadline")
                    var tenggatString = ""

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

                                if(tenggat != null){
                                    if(tenggat > date){
                                        tenggatString = changeDate(tenggat)
                                        var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggatString)
                                        listJob.add(job)
                                    }
                                }

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
            }
            }
    }

    private fun getJobWithInterest(interest : String){
        getRecommendJob(interest)
//        getOtherJobWithInterest(interest)
    }

    private fun getRecommendJob(interest: String) {
        binding.progressBar.isVisible = true
        var date = getDateToday()

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
                    var tenggat = doc.getLong("job_deadline")
                    var tenggatString = ""

                    if (tenggat != null){
                        tenggatString = changeDate(tenggat)
                    }

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

                                if(tenggat != null){
                                    if(tenggat > date){
                                        var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggatString)
                                        listRecomJob.add(job)
                                    }
                                }
                                setRecomJob(listRecomJob)
                                getOtherJobWithInterest(interest)
                            }
                    }
                }
                if (value.isEmpty){
                    binding.titleRecommend.isVisible = false
                    binding.titleOtherJob.isVisible = false
                    binding.rvRecomJobList.isVisible = false
                    binding.progressBar.isVisible = false

                    getOtherJobWithInterest(interest)
                }
            }
    }

    private fun setRecomJob(listRecomJob : ArrayList<ItemJob>){
        if (listRecomJob.isEmpty()) {
            binding.titleRecommend.isVisible = false
            binding.titleOtherJob.isVisible = false
            binding.rvRecomJobList.isVisible = false
            binding.progressBar.isVisible = false
        }
        else {
            listRecomJobAdapter.setData(listRecomJob)
            binding.progressBar.isVisible = false
        }
    }

    private fun getOtherJobWithInterest(interest : String){
        // get other job with interest
        var date = getDateToday()
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
                    var tenggat = doc.getLong("job_deadline")
                    var tenggatString = ""

                    if (tenggat != null){
                        tenggatString = changeDate(tenggat)
                    }

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

                                if(tenggat != null){
                                    if(tenggat > date){
                                        var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggatString)
                                        listJob.add(job)
                                    }
                                }

                                setOtherJob(listJob)
                            }
                    }
                }
                if (value.isEmpty){
                    binding.tvNothingJob.isVisible = true
                    binding.titleOtherJob.isVisible = true
//                    binding.rvOtherJobList.isVisible = false
                    binding.progressBar.isVisible = false
                }
            }
    }

    private fun setOtherJob(listJob: ArrayList<ItemJob>){
        if (listJob.isEmpty()) {
//            binding.tvNothingJob.isVisible = true
            binding.titleOtherJob.isVisible = true
//            binding.rvOtherJobList.isVisible = false
            binding.progressBar.isVisible = false
        } else {
            listJobAdapter.setData(listJob)
            binding.progressBar.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        val id_user = LoginPref(requireActivity()).getIdMhs().toString()
        setProfile (id_user)

        binding.searchBox.text = null
    }
}