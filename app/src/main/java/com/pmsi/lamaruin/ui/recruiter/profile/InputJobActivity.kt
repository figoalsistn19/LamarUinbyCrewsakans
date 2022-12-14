package com.pmsi.lamaruin.ui.recruiter.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.isVisible
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.MainRecuiterActivity
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.CreateStudent
import com.pmsi.lamaruin.data.model.JobVacancy
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityInputJobBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class InputJobActivity : AppCompatActivity() {

    private var posisi_user: String? = "Marketing"

    private lateinit var binding: ActivityInputJobBinding

    @Inject
    lateinit var service: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val listPosisi = resources.getStringArray(R.array.posisi_list)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, listPosisi)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                posisi_user = listPosisi[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        binding.btnSubmitJob.setOnClickListener { inputJob() }

    }
    private fun inputJob() {

        val jobName = binding.etJobName.text.toString()
        val jobDesc = binding.etJobDesc.text.toString()
        val qualification = binding.etQualification.text.toString()
        val jobCategory = posisi_user.toString()

        when {
            jobName.isEmpty() -> {
                binding.etJobName.error = "Masukan Job Name"
            }
            jobDesc.isEmpty() -> {
                binding.etJobDesc.error = "Masukkan Job Description"
            }
            qualification.isEmpty() -> {
                binding.etQualification.error = "Masukkan Qualification"
            }

            else -> {
                var id_user = LoginPref(this).getIdMhs().toString()

                val job = JobVacancy(
                    job_name = jobName,
                    job_desc = jobDesc,
                    job_category = jobCategory,
                    qualification = qualification,
                    id_recruiter = id_user
                )
                service.addJob(job) { id ->
                    Toast.makeText(
                        this@InputJobActivity,
                        "Berhasil Submit Job",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this, MainRecuiterActivity::class.java)
                    startActivity(intent)
                }
                }

            }
        }
}