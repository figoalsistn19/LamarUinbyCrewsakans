package com.pmsi.lamaruin.ui.mahasiswa.listJob.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayoutMediator
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.databinding.ActivityDetailJobBinding
import com.pmsi.lamaruin.databinding.ActivityMainMahasiswaBinding
import com.pmsi.lamaruin.utils.hideKeyboard

class DetailJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val sectionsPagerAdapter = SectionsPagerAdapter(this
        )
        binding.viewPagerDetailJob.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tbDetailJob, binding.viewPagerDetailJob) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_detail_1,
            R.string.tab_detail_2
        )
    }
}