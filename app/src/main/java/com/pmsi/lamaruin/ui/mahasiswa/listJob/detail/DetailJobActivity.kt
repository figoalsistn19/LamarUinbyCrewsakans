package com.pmsi.lamaruin.ui.mahasiswa.listJob.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import coil.load
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import com.google.android.material.tabs.TabLayoutMediator
import com.pmsi.lamaruin.R
import androidx.fragment.app.FragmentTransaction
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.AppliedJob
import com.pmsi.lamaruin.data.model.CreateStudent
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.model.ItemListPelamar
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityDetailJobBinding
import com.pmsi.lamaruin.databinding.ActivityMainMahasiswaBinding
import com.pmsi.lamaruin.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class DetailJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailJobBinding

    @Inject
    lateinit var service: FirestoreService



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val id_recruiter = intent.getStringExtra("id_recruiter")
        val id_job = intent.getStringExtra("id_job")

        checkPelamar(id_job)

        if(id_job != null && id_recruiter != null){
            setProfile(id_job, id_recruiter)
        }

        binding.btnLamar.setOnClickListener{
            whenBtnLamarClick(id_job, id_recruiter)
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setProfile(id_job: String, id_recruiter: String){

        var foto : String?= ""
        var company_name: String? = ""
        var company_city: String? = ""
        var company_desc: String? = ""
//         get company profile
        service.getRecruiterById(id_recruiter)
            .get()
            .addOnSuccessListener {
                foto = it.getString("foto")
                company_name = it.getString("company_name")
                company_city = it.getString("company_address")
                company_desc = it.getString("company_profile")

                binding.itemFotoCompany.load(foto)
                binding.itemCompanyName.text = company_name
                binding.itemCompanyLocation.text = company_city
                binding.tvDeskripsiCompany.text = company_desc
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
                var job_qualification = it.getString("qualification")
                var job_desc = it.getString("job_desc")
                var tenggat = "12-12-2022" // ini sementara

                binding.tvJobTitle.text = job_title
                binding.itemDeadline.text = tenggat
                binding.tvKualifikasi.text = job_qualification
                binding.tvDeskripsiJob.text = job_desc
            }
    }

    private fun whenBtnLamarClick(id_job: String?, id_recruiter: String?) {
        CustomDialog(this).show(
            getString(R.string.alert_apply_title),
            getString(R.string.are_you_sure_apply)
        ) {

            var id_student = LoginPref(this).getIdMhs()

            if (it.toString() == "YES") {
                binding.progressBar.isVisible = true

                var nama_student = ""
                var email_student = ""
                var foto_student = ""
                var foto_company = ""
                var nama_company = ""
                var posisi_job = ""

                // get detail student
                service.searchUsersById(id_student!!)
                    .get()
                    .addOnSuccessListener {
                        nama_student = it.getString("name").toString()
                        email_student = it.getString("email").toString()
                        foto_student = it.getString("foto").toString()
                    }

                // get detail company
                service.getRecruiterById(id_recruiter!!)
                    .get()
                    .addOnSuccessListener {
                        foto_company = it.getString("foto").toString()
                        foto_company = it.getString("company_name").toString()
                    }

                // get posisi job
                service.getJobById(id_job!!)
                    .get()
                    .addOnSuccessListener {
                        posisi_job = it.getString("job_name").toString()

                        val item_list_pelamar = ItemListPelamar(
                            id_applied_job = id_job,
                            id_pelamar = id_student,
                            foto = foto_student,
                            email = email_student,
                            nama = nama_student
                        )

                        val applied_job = AppliedJob(
                            status = "Pending",
                            id_student = id_student,
                            id_job = id_job,
                            company_name = nama_company,
                            company_photo = foto_company,
                            job_title = posisi_job

                        )

                        // create collection applied job
                        service.addAppliedJob(applied_job) {

                            // update collection job
                            service.addPelamar(id_job!!, item_list_pelamar)

                            binding.progressBar.isVisible = false

                            val i = Intent(this, MainMahasiswaActivity::class.java)
                            i.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)

                            Toast.makeText(
                                this,
                                "Lamaran Terkirim",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun checkPelamar(id_job: String?){
        var id_student = LoginPref(this).getIdMhs()
        service.checkPelamar(id_job!!, id_student!!)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
                    return@addSnapshotListener
                }
                var nama_pelamar : String? = ""
                for (doc in value!!) {
                    nama_pelamar = doc.getString("name")
                }
                if(nama_pelamar != ""){
                    binding.btnLamar.isVisible = false

                    val param = binding.scrollView.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,480,0,0)
                    binding.scrollView.layoutParams = param
                }
            }
    }
}