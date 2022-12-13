package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityAddEducationBinding
import com.pmsi.lamaruin.databinding.ActivityAddExperienceBinding
import com.pmsi.lamaruin.utils.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddExperienceActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {

    lateinit var startDate : String
    lateinit var endDate : String

    @Inject
    lateinit var service: FirestoreService

    private lateinit var binding : ActivityAddExperienceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun saveData(){
        val id_user = LoginPref(this).getIdMhs()

        val posisi = binding.editPosisi.text.toString()
        val company = binding.editCompany.text.toString()
        val desc = binding.editDescription.text.toString()


        when {
            posisi.isEmpty() -> {
                binding.editPosisi.error = "Input Posisi"
            }
            company.isEmpty() -> {
                binding.editCompany.error = "Input Company"
            }
            desc.isEmpty() -> {
                binding.editDescription.error = "Input School"
            }
            startDate == "" -> {
                Toast.makeText(
                    this, "Input Start Date", Toast.LENGTH_LONG).show()
            }
            endDate == "" -> {
                Toast.makeText(
                    this, "Input End Date", Toast.LENGTH_LONG).show()
            }
            else -> {
                val education = Experience(company, posisi, desc, startDate, endDate)
                if(id_user != null){
                    service.addExperience(id_user,education){
                        finish()
                    }
                }
            }
        }
    }

    fun showDateStartPicker(view: View) {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "startDatePicker")
    }

    fun showDateEndPicker(view: View) {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "endDatePicker")
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        var date = dateFormat.format(calendar.time)
//        dueDateMillis = calendar.timeInMillis

        if (tag == "startDatePicker"){
            startDate = yearFormat.format(calendar.time).toString()
            binding.addTvStartDate.text = date

        } else {
            endDate = yearFormat.format(calendar.time).toString()
            binding.addTvEndDate.text = date
        }
    }
}