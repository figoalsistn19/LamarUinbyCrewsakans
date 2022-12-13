package com.pmsi.lamaruin.ui.recruiter.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.databinding.ActivityInputJobBinding

class InputJobActivity : AppCompatActivity() {

    private var posisi_user: String? = "Agent"

    private lateinit var binding: ActivityInputJobBinding
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
    }
}