package com.pmsi.lamaruin.ui.mahasiswa.listJob.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pmsi.lamaruin.MainMahasiswaActivity
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityRegisterMahasiswaBinding
import com.pmsi.lamaruin.databinding.ActivitySearchJobBinding
import com.pmsi.lamaruin.ui.mahasiswa.listJob.detail.DetailJobActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SearchJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchJobBinding

    @Inject
    lateinit var service: FirestoreService

    private val listSearchJobAdapter : ListSearchJobAdapter by lazy {
        ListSearchJobAdapter{
            Intent(this, DetailJobActivity::class.java).apply {
                putExtra("id_job", it.id_job)
                putExtra("id_recruiter", it.id_recruiter)
                startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val text_search = intent.getStringExtra("text_search")
        if (text_search != null){
            searchJob(text_search)
        }
        binding.tvSearchJob.text = text_search.toString().toEditable()

        binding.apply {
            rvSearchJobList.apply {
                layoutManager = LinearLayoutManager(this@SearchJobActivity)
                adapter = listSearchJobAdapter
                setHasFixedSize(true)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
//            val i = Intent(this, MainMahasiswaActivity::class.java)
//            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(i)
        }

        binding.btnSearch.setOnClickListener {
            var text = binding.tvSearchJob.text.toString()
            searchJob(text)
        }
    }

    private fun searchJob(text: String){
        binding.progressBar.isVisible = true
        var date = getDateToday()

        service.getSearchJob(text)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listJob = ArrayList<ItemJob>()
                for (doc in value!!) {
                    var id_job = doc.getString("id_job")
                    var id_recruiter = doc.getString("id_recruiter")
                    var cat = doc.getString("job_category")
                    var name = doc.getString("job_name")
                    var tenggat = doc.getLong("job_deadline")
                    var tenggatString = ""

                    var foto: String? = ""
                    var company_name: String? = ""
                    var company_city: String? = ""

//                    get company profile
                    if (id_recruiter != null) {
                        service.getRecruiterById(id_recruiter)
                            .get()
                            .addOnSuccessListener {
                                foto = it.getString("foto")
                                company_name = it.getString("company_name")
                                company_city = it.getString("company_address")

                                if(tenggat != null){
                                    if( tenggat > date){
                                        tenggatString = changeDate(tenggat)

                                        var job = ItemJob(
                                            id_job,
                                            id_recruiter,
                                            name,
                                            cat,
                                            company_name,
                                            company_city,
                                            foto,
                                            tenggatString
                                        )
                                        listJob.add(job)
                                    }
                                }

                                if (listJob.isEmpty()) {
                                    binding.progressBar.isVisible = false
                                    binding.tvNothingSearchJob.isVisible = true
                                } else {
                                    listSearchJobAdapter.setData(listJob)
                                    binding.progressBar.isVisible = false
                                    binding.tvNothingSearchJob.isVisible = false
                                }
                            }
                    }
                }
                if (value.isEmpty){
                    binding.tvNothingSearchJob.isVisible = true
                    binding.progressBar.isVisible = false
                }
            }
    }

    fun getDateToday(): Long {
        var dueDateMillis: Long = System.currentTimeMillis()
        return dueDateMillis
    }

    fun changeDate(date: Long) : String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date = dateFormat.format(date)
        return date
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}