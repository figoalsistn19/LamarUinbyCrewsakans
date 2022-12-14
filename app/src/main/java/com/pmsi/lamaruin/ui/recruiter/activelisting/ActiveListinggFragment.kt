package com.pmsi.lamaruin.ui.recruiter.activelisting

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
import com.pmsi.lamaruin.databinding.FragmentActiveListinggBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import com.pmsi.lamaruin.ui.recruiter.activelisting.detail.ApplicantListActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ActiveListinggFragment : Fragment() {

    private lateinit var binding: FragmentActiveListinggBinding

    private val activeListinggAdapter : ActiveListingAdapter by lazy {
        ActiveListingAdapter{
            Intent(requireActivity(), ApplicantListActivity::class.java).apply {
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
        binding = FragmentActiveListinggBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvActiveListing.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = activeListinggAdapter
                setHasFixedSize(true)
            }
        }
        getJobByRecruiterId()

    }

    private fun getJobByRecruiterId(){
        binding.progressBar.isVisible = true
        var date = getDateToday()

        val id_user = LoginPref(requireActivity()).getIdMhs().toString()
        service.getJobByRecruiterId(id_user)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Timber.d("Listen failed.")
                        binding.progressBar.isVisible = false
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

                                        if(tenggat != null){
                                            if(tenggat > date){
                                                var job = ItemJob(id_job,id_recruiter,name,cat,company_name,company_city,foto,tenggats)
                                                itemJob.add(job)
                                            }
                                        }

                                        if (itemJob.isEmpty()) {
                                            binding.progressBar.isVisible = false
                                        } else {
                                            activeListinggAdapter.setData(itemJob)
                                            binding.progressBar.isVisible = false

                                        }
                                    }
                        }
                    }
                    if (value.isEmpty){
                        binding.progressBar.isVisible = false
                    }
                }
    }

    fun getDateToday(): Long {
        var dueDateMillis: Long = System.currentTimeMillis()
        return dueDateMillis
    }

    private fun changeDate(date: Long) : String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = dateFormat.format(date)
        return date
    }

    override fun onResume() {
        super.onResume()
        getJobByRecruiterId()
    }

}