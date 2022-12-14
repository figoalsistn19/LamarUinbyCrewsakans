package com.pmsi.lamaruin.ui.recruiter.listJob

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
import com.pmsi.lamaruin.databinding.FragmentListJobRecBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.ListJobAdapter
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import com.pmsi.lamaruin.ui.mahasiswa.listJob.search.ListSearchJobAdapter
import com.pmsi.lamaruin.ui.mahasiswa.listJob.search.SearchJobActivity
import com.pmsi.lamaruin.ui.recruiter.activelisting.ActiveListingAdapter
import com.pmsi.lamaruin.ui.recruiter.activelisting.detail.ApplicantListActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ListJobRecFragment : Fragment() {

    private lateinit var binding: FragmentListJobRecBinding

    private val activeListinggAdapter : ActiveListingAdapter by lazy {
        ActiveListingAdapter{
            Intent(requireActivity(), DetailJobRecActivity::class.java).apply {
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
        binding = FragmentListJobRecBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getJobByRecruiterId()

        binding.apply {
            rvJobList.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = activeListinggAdapter
                setHasFixedSize(true)
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

    fun getFirstName(name: String): String {
        val names = name.trim().split(Regex("\\s+"))
        return names.first()
    }

    private fun getJobByRecruiterId(){
        val id_user = LoginPref(requireActivity()).getIdMhs().toString()
        service.getJobByRecruiterId(id_user)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var itemJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = doc.getLong("job_deadline")
                    var tenggats = changeDate(tenggat!!)

                    var foto : String? = ""
                    var company_name : String? = ""
                    var company_city : String? = ""

//                    get company profile
                    if(id_recruiter != null){
                        service.getRecruiterById(id_user)
                            .get()
                            .addOnSuccessListener {
                                foto = it.getString("foto")
                                company_name = it.getString("company_name")
                                company_city = it.getString("company_address")

                                var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggats)
                                itemJob.add(job)

                                if (itemJob.isEmpty()) {
                                } else {
                                    activeListinggAdapter.setData(itemJob)

                                }
                            }
                    }

                    // buat sementara get company profile
//                    foto = "https://karier.uinjkt.ac.id/public/main/1616805063_logo_portrait.png"
//                    company_name = "Pusat Karier UIN Jakarta"
//                    company_city = "Tanggerang Selatan"
                }
                        if (value.isEmpty){

                        }
            }
    }

    private fun changeDate(date: Long) : String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = dateFormat.format(date)
        return date
    }

//    override fun onResume() {
//        super.onResume()
//        val id_user = LoginPref(requireActivity()).getIdMhs().toString()
//        setProfile (id_user)
//
//        binding.searchBox.text = null
//    }
}