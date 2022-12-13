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

    private lateinit var binding: ActivityInputJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // access the items of the list
        val languages = resources.getStringArray(R.array.Languages)

        // access the spinner
        val spinner = binding.spinner
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    Toast.makeText(this@InputJobActivity,
                        getString(R.string.selected_item) + " " +
                                "" + languages[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }
}