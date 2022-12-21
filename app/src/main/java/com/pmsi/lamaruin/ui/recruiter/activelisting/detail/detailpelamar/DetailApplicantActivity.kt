package com.pmsi.lamaruin.ui.recruiter.activelisting.detail.detailpelamar

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.data.model.ItemListPelamar
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.ActivityDetailApplicantBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailApplicantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailApplicantBinding

    @Inject
    lateinit var service: FirestoreService

    val storage = Firebase.storage("gs://lamaruin-92998.appspot.com")

    private val listEditEduRecAdapter : ListEditEduRecAdapter by lazy {
        ListEditEduRecAdapter()
    }

    private val listEditExpRecAdapter : ListEditExpRecAdapter by lazy {
        ListEditExpRecAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailApplicantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        var id = intent.getStringExtra("id_pelamar")
        var idAppliedJob = intent.getStringExtra("id_applied_job")
        setProfile(id!!)

        binding.btnAcc.setOnClickListener{
            statusApplicantAcc()
            onBackPressed()
        }

        binding.tvNamaCV.setOnClickListener{
                getUrlCV(id)
        }

        binding.btnReject.setOnClickListener {
            statusApplicantRej()
            onBackPressed()
        }

    }

    private fun statusApplicantAcc(){
        val status = "Accepted"
        val id_applicant = intent.getStringExtra("id_applied_job")
        service.updateApplicant(id_applicant!!)
            .update("status",status)
            .addOnSuccessListener {
                Timber.d("Sukses update status applicant ke firestore")
            }
            .addOnFailureListener { e ->
                Timber.tag(ContentValues.TAG).w(e, "Gagal update status applicant ke firestore")
            }
    }

    private fun statusApplicantRej(){
        val id_applicant = intent.getStringExtra("id_applied_job")
        val status = "Rejected"
        service.updateApplicant(id_applicant!!)
            .update("status",status)
            .addOnSuccessListener {
                Timber.d("Sukses update status applicant ke firestore")

            }
            .addOnFailureListener { e ->
                Timber.tag(ContentValues.TAG).w(e, "Gagal update status applicant ke firestore")
            }
    }

    private fun setProfile(id: String){

        binding.apply {
            rvListEducation.apply {
                layoutManager = LinearLayoutManager(this@DetailApplicantActivity)
                adapter = listEditEduRecAdapter
                setHasFixedSize(true)
            }

            rvListExperience.apply {
                layoutManager = LinearLayoutManager(this@DetailApplicantActivity)
                adapter = listEditExpRecAdapter
                setHasFixedSize(true)
            }
        }

        binding.progressBar.isVisible = true
        service.searchUsersById(id)
            .get()
            .addOnSuccessListener {
                var pelamar = ArrayList<ItemListPelamar>()
                var id_student = it.getString("id_student")
                var nama = it.getString("name")
                var email = it.getString("email")
                var foto = it.getString("foto")
                var description = it.getString("description")
                var address = it.getString("address")
                var phone = it.getString("phone")
                var interest = it.getString("interest")
                var skill = it.getString("skill")
                var nama_cv = it.getString("nama_cv")
                var link_cv = it.getString("link_cv")

                binding.ivProfile.load(foto){
                    transformations(CircleCropTransformation())
                }

                binding.apply{
                    tvNamaMahasiswa.text = nama
                    tvEmailMhs.text = email
                    tvAddressUser.text = address
                    tvDescUser.text = description
                    tvPhoneUser.text = phone
                    tvInterestUser.text = interest
                    tvSkillUser.text = skill
                    tvNamaCV.text = nama_cv
                }
                if(description == ""){ binding.tvDescUser.text = "Not yet Added" }
                if(address == ""){ binding.tvAddressUser.text = "Not yet Added" }
                if(phone == ""){ binding.tvPhoneUser.text = "Not yet Added" }
                if(interest == ""){ binding.tvInterestUser.text = "Not yet Added" }
                if(skill == ""){ binding.tvSkillUser.text = "Not yet Added" }
                if(nama_cv == ""){ binding.tvNamaCV.text = "No file chosen" }

                binding.progressBar.isVisible = false
            }
        binding.addEducation.isVisible = false
        binding.addExperience.isVisible = false
        service.getEdu(id)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listEdu = java.util.ArrayList<Education>()
                for (doc in value!!) {
                    var jurusan = doc.getString("field_of_study")
                    var degree = doc.getString("degree")
                    var univ = doc.getString("school")
                    var id_edu = doc.getString("id_edu")
                    var start_year = doc.getString("education_start_date")
                    var end_year = doc.getString("education_end_date")
                    var edu = Education(jurusan, degree, univ,start_year, end_year, id_edu)
                    listEdu.add(edu)
                }
                if (listEdu.isEmpty()) {
                    binding.tvNothingEdu.isVisible = true
                    binding.rvListEducation.isVisible = false
                } else {
                    binding.tvNothingEdu.isVisible = false
                    binding.rvListEducation.isVisible = true
                    listEditEduRecAdapter.setData(listEdu)
                }
            }

        service.getExp(id)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listExp = java.util.ArrayList<Experience>()
                for (doc in value!!) {
                    var title = doc.getString("title")
                    var role = doc.getString("role")
                    var desc = doc.getString("experience_desc")
                    var exp_start_date = doc.getString("experience_start_date")
                    var exp_end_date = doc.getString("experience_end_date")
                    var id_experience = doc.getString("id_experience")
                    var exp = Experience(title, role, desc, exp_start_date, exp_end_date, id_experience)
                    listExp.add(exp)
                }
                if (listExp.isEmpty()) {
                    binding.tvNothingExp.isVisible = true
                    binding.rvListExperience.isVisible = false
                } else {
                    binding.tvNothingExp.isVisible = false
                    binding.rvListExperience.isVisible = true
                    listEditExpRecAdapter.setData(listExp)
                }

                binding.progressBar.isVisible = false
            }
    }

    private fun getUrlCV(id: String) {

        service.searchUsersById(id)
            .get()
            .addOnSuccessListener {
                var url_cv = it.getString("url_cv")
                var nama_cv = it.getString("nama_cv")
                storage.getReferenceFromUrl(url_cv!!)
                    .downloadUrl.addOnSuccessListener {
                        var urls = it.toString()
                        downloadFile(this@DetailApplicantActivity, nama_cv!!,
                            Environment.DIRECTORY_DOWNLOADS, urls)
                    }.addOnFailureListener {
                        // Handle any errors
                    }
            }


//        val rootPath = File(Environment.getExternalStorageDirectory(), "file_name")
//        if (!rootPath.exists()) {
//            rootPath.mkdirs()
//        }
//        val localFile = File(rootPath, "fileCv.txt")
//        var id_user = LoginPref(requireActivity()).getIdMhs().toString()
//
//        service.searchUsersById(id_user)
//            .get()
//            .addOnSuccessListener {
//                var url_cv = it.getString("url_cv")
//                storage.getReferenceFromUrl(url_cv!!)
//                    .getFile(localFile).addOnSuccessListener {
//                        Log.e("firebase ", ";local tem file created  created $localFile")
//                    }.addOnFailureListener {
//                        fun onFailure(@NonNull exception: Exception) {
//                            Log.e("firebase ", ";local tem file not created  created $exception")
//                        }
//                    }
//            }
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getStringExtra("id_pelamar")
        setProfile(id!!)
    }

    private fun downloadFile(context: Context, fileName: String, destinationDirectory: String, urls: String) {
        var url = Uri.parse(urls)
        var downloadManager : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var request = DownloadManager.Request(url)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName)

        downloadManager.enqueue(request)
//        url.openStream().use {
//            Channels.newChannel(it).use { rbc ->
//                FileOutputStream(outputFileName).use { fos ->
//                    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
//                }
//            }
//        }
    }
}