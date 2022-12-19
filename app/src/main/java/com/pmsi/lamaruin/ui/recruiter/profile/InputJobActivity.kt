package com.pmsi.lamaruin.ui.recruiter.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pmsi.lamaruin.MainRecuiterActivity
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.JobVacancy
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityInputJobBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint

class InputJobActivity : AppCompatActivity() {

    private var posisi_user: String? = "Marketing"

    private lateinit var binding: ActivityInputJobBinding

    @Inject
    lateinit var service: FirestoreService

    var cal = Calendar.getInstance()

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

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        binding.tvDate!!.text = "--/--/----"

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        binding.datePicker!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@InputJobActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        binding.btnSubmitJob.setOnClickListener { inputJob() }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvDate!!.text = sdf.format(cal.getTime())
    }

    private fun inputJob() {

        val jobName = binding.etJobName.text.toString()
        val jobDesc = binding.etJobDesc.text.toString()
        val qualification = binding.etQualification.text.toString()
        val jobCategory = posisi_user.toString()
        val jobDeadline = binding.tvDate.text.toString()

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
                    id_recruiter = id_user,
                    job_deadline = jobDeadline
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