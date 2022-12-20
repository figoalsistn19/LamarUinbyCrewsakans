package com.pmsi.lamaruin.ui.recruiter.activelisting.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.pmsi.lamaruin.data.model.ItemListPelamar
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityApplicantListBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.ListJobAdapter
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import com.pmsi.lamaruin.ui.recruiter.activelisting.detail.detailpelamar.DetailPelamarFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ApplicantListActivity() : AppCompatActivity() {

    private val applicantListAdapter: ApplicantListAdapter by lazy {
        val listener: MutableList<ItemListPelamar>
        ApplicantListAdapter()
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

        // get company profile sementara
//        var foto = "https://karier.uinjkt.ac.id/public/main/1616805063_logo_portrait.png"
//        var company_name = "Pusat Karier UIN Jakarta"
//        var company_city = "Tanggerang Selatan"
//        var company_desc = "A company profile describes what makes your company unique. It automatically differentiates your brand because no other company has the exact same founding story and reason for existing that your business does. Your history and values are integral parts of your brand positioning strategy, and a company profile is where you can mention this information without it feeling extraneous or out of place. You can justify a higher price point for your products and services, if you go into details about your production values or ethically-sourced materials.\n" +
//                "\n" +
//                "For instance, Starbucks’ coffee may not necessarily be better than Dunkin’ Donuts’ coffee, but because Starbucks goes into details about its high-quality ingredients, it immediately creates the sense that you’ll be paying a little more for a \"better\" product."

//        binding.itemFotoCompany.load(foto)
//        binding.itemCompanyName.text = company_name
//        binding.itemCompanyLocation.text = company_city
//        binding.tvDeskripsiCompany.text = company_desc

        // get detail job
        service.getJobById(id_job)
            .get()
            .addOnSuccessListener {
                var job_title = it.getString("job_name")
                var tenggat = it.getLong("job_deadline").toString()

                binding.tvJobName.text = job_title
                binding.tvJobDeadline.text = tenggat
            }
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
                    var id_job = doc.getString("id_job")
                    var foto = doc.getString("foto")
                    var nama = doc.getString("nama")
                    var email = doc.getString("email")

                    var job = ItemListPelamar(
                        nama = nama,
                        foto = foto,
                        email = email
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