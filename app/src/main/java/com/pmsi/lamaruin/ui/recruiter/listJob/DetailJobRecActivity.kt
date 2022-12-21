package com.pmsi.lamaruin.ui.recruiter.listJob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityDetailJobRecBinding
import com.pmsi.lamaruin.databinding.FragmentListJobRecBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailJobRecActivity : AppCompatActivity() {

    @Inject
    lateinit var service: FirestoreService

    private lateinit var binding: ActivityDetailJobRecBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJobRecBinding.inflate(layoutInflater)
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
    }

    private fun setProfile(id_job: String, id_recruiter: String){

        var foto : String?= ""
        var company_name: String? = ""
        var company_address: String? = "-"
        var company_desc: String? = ""
//         get company profile
        service.getRecruiterById(id_recruiter)
            .get()
            .addOnSuccessListener {
                foto = it.getString("foto")
                company_name = it.getString("company_name")
                company_address = it.getString("company_address")
                company_desc = it.getString("company_profile")


                binding.itemFotoCompany.load(foto)
                binding.itemCompanyName.text = company_name
                binding.itemCompanyLocation.text = company_address
                binding.tvDeskripsiCompany.text = company_desc
            }

        // get detail job
        service.getJobById(id_job)
            .get()
            .addOnSuccessListener {

                var job_title = it.getString("job_name")
                var tenggat = it.getLong("job_deadline")
                var tenggats = changeDate(tenggat!!)
                var job_qualification = it.getString("qualification")
                var job_desc = it.getString("job_desc")

                binding.tvJobTitle.text = job_title
                binding.itemDeadline.text = "Dibuka sampai $tenggats"
                binding.tvKualifikasi.text = job_qualification
                binding.tvDeskripsiJob.text = job_desc
            }

    }

    private fun changeDate(date: Long) : String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = dateFormat.format(date)
        return date
    }
}