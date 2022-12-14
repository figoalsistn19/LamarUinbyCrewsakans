package com.pmsi.lamaruin.ui.mahasiswa.listJob.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import com.pmsi.lamaruin.R
import androidx.fragment.app.FragmentTransaction
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityDetailJobBinding
import com.pmsi.lamaruin.databinding.ActivityMainMahasiswaBinding
import com.pmsi.lamaruin.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
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

        if(id_job != null && id_recruiter != null){
            setProfile(id_job, id_recruiter)
            sendDataJobtoFragment(id_job)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this
        )
        binding.viewPagerDetailJob.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tbDetailJob, binding.viewPagerDetailJob) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun setProfile(id_job: String, id_recruiter: String){

        // get company profile
//        service.getRecruiterById(id_recruiter)
//            .get()
//            .addOnSuccessListener {
//                var foto = it.getString("foto")
//                var company_name = it.getString("name")
//                var company_city = it.getString("city")
//
//                binding.itemFotoCompany.load(foto)
//                binding.itemCompanyName.text = company_name
//                binding.itemCompanyLocation.text = company_city
//            }

        // get company profile sementara
        var foto = "https://karier.uinjkt.ac.id/public/main/1616805063_logo_portrait.png"
        var company_name = "Pusat Karier UIN Jakarta"
        var company_city = "Tanggerang Selatan"

        binding.itemFotoCompany.load(foto)
        binding.itemCompanyName.text = company_name
        binding.itemCompanyLocation.text = company_city

        // get detail job
        service.getJobById(id_job)
            .get()
            .addOnSuccessListener {
                var job_title = it.getString("job_name")
                var tenggat = "12-12-2022" // ini sementara

                binding.tvJobTitle.text = job_title
                binding.itemDeadline.text = tenggat
            }
    }
    private fun sendDataJobtoFragment(id_job: String){
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = JobDescriptionFragment()

        val bundle = Bundle()
        bundle.putString("id_job", id_job)
        myFragment.arguments = bundle
        fragmentTransaction.add(binding.viewPagerDetailJob, myFragment).commit()
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_detail_1,
            R.string.tab_detail_2
        )
    }
}