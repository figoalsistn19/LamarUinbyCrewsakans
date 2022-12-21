package com.pmsi.lamaruin.ui.recruiter.activelisting.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.pmsi.lamaruin.data.model.ItemListPelamar
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityApplicantListBinding
import com.pmsi.lamaruin.ui.recruiter.activelisting.detail.detailpelamar.DetailApplicantActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ApplicantListActivity() : AppCompatActivity() {

    private val applicantListAdapter: ApplicantListAdapter by lazy {
        ApplicantListAdapter{
            Intent(this@ApplicantListActivity, DetailApplicantActivity::class.java).apply {
                putExtra("id_pelamar", it.id_pelamar)
                putExtra("id_applied_job",it.id_applied_job)
                startActivity(this)
            }
        }
    }

    @Inject
    lateinit var service: FirestoreService

    private lateinit var binding: ActivityApplicantListBinding

    //    by lazy {
//        var listPelamar =
//        ApplicantListAdapter(listener = Lis)
////        {
////            Intent(requireActivity(), DetailPelamarFragment::class.java).apply {
////                putExtra("id_job", it.id_job)
////                putExtra("id_recruiter", it.id_recruiter)
////                startActivity(this)
////            }
////        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicantListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener{
            onBackPressed()
    }

        val id_recruiter = intent.getStringExtra("id_recruiter")
        val id_job = intent.getStringExtra("id_job")

        if(id_job != null && id_recruiter != null){
            setProfile(id_job, id_recruiter)
        }

        binding.apply {
            rvApplicantList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = applicantListAdapter
                setHasFixedSize(true)
            }
        }

        getPelamarByJobId()
    }

    private fun setProfile(id_job: String, id_recruiter: String){

        var foto : String?= ""
        var company_name: String? = ""
//         get company profile
        service.getRecruiterById(id_recruiter)
            .get()
            .addOnSuccessListener {
                foto = it.getString("foto")
                company_name = it.getString("company_name")

                binding.ivCompany.load(foto)
                binding.tvCompanyName.text = company_name
            }

        // get detail job
        service.getJobById(id_job)
            .get()
            .addOnSuccessListener {

                var job_title = it.getString("job_name")
                var tenggat = it.getLong("job_deadline")
                var tenggats = changeDate(tenggat!!)

                binding.tvJobName.text = job_title
                binding.tvJobDeadline.text = tenggats
            }

    }

    private fun changeDate(date: Long) : String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = dateFormat.format(date)
        return date
    }

    private fun getPelamarByJobId(){
        binding.progressBar.isVisible = true
        val id_recruiter = intent.getStringExtra("id_recruiter")
        val id_job = intent.getStringExtra("id_job")
        service.getPelamar(id_job!!)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var itemJob = ArrayList<ItemListPelamar>()
                for (doc in value!!) {
                    var id_pelamar = doc.getString("id_pelamar")
                    var id_job = doc.getString("id_job")
                    var id_applied_job = doc.getString("id_applied_job")
                    var foto = doc.getString("foto")
                    var nama = doc.getString("nama")
                    var email = doc.getString("email")

                    var job = ItemListPelamar(
                        id_pelamar = id_pelamar,
                        nama = nama,
                        foto = foto,
                        email = email,
                        id_applied_job = id_applied_job
                    )
                    itemJob.add(job)

                    if (itemJob.isEmpty()) {
                        binding.tvNothingJobAdded.isVisible = true
                        binding.progressBar.isVisible = false
                    } else {
                        applicantListAdapter.setData(itemJob)
                        binding.progressBar.isVisible = false
                    }
                }
            }
    }
}