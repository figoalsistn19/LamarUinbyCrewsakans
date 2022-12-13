package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentProfilBinding
import com.pmsi.lamaruin.register.RegisterMahasiswaActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProfilFragment : Fragment() {

    private lateinit var binding : FragmentProfilBinding

    private var statAddProfile : Boolean? = null

    @Inject
    lateinit var service: FirestoreService

    val storage = Firebase.storage("gs://lamaruin-92998.appspot.com")
    private var uri: Uri? = null
    private var nameCv: String? = ""
    private var getAvatar: File? = null
    private var uriAvatar: Uri? = null

    private val listEditEduAdapter : ListEditEduAdapter by lazy {
        ListEditEduAdapter()
    }

    private val listEditExpAdapter : ListEditExpAdapter by lazy {
        ListEditExpAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = LoginPref(requireActivity()).getIdMhs().toString()
        setProfil(id)

        binding.apply {
            logout.setOnClickListener { logout() }
            btnAddCV.setOnClickListener{ addCV() }
            changeAvatar.setOnClickListener { startGallery() }
            tvNamaCV.setOnClickListener{
                if (tvNamaCV.text != ""){
                    Intent(requireActivity(), DetailCvActivity::class.java).apply {
                        putExtra("data", nameCv)
                        startActivity(this)
                    }
                }
            }
            editProfile.setOnClickListener {
                val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                startActivity(intent)
            }
            addEducation.setOnClickListener{
                val intent = Intent(requireActivity(), AddEducationActivity::class.java)
                startActivity(intent)
            }
            addExperience.setOnClickListener {
                val intent = Intent(requireActivity(), AddExperienceActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addCV(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, 777)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777) {
            data?.data?.let {
                uri=it
            }
            uri?.let {
                nameCv = getFileNameFromUri(requireContext(),it)
                uploadtoStorage(it, nameCv)
            }
        }
    }

    private fun setProfil(id : String){
        binding.progressBar.isVisible = true

        binding.apply {
            rvListEducation.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listEditEduAdapter
                setHasFixedSize(true)
            }

            rvListExperience.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = listEditExpAdapter
                setHasFixedSize(true)
            }
        }

        service.searchUsersById(id)
            .get()
            .addOnSuccessListener{
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
//                    tvSilakanLengkapi.isVisible = false
                }

                if(description == ""){ binding.tvDescUser.text = "Not yet Added" }
                if(address == ""){ binding.tvAddressUser.text = "Not yet Added" }
                if(phone == ""){ binding.tvPhoneUser.text = "Not yet Added" }
                if(interest == ""){ binding.tvInterestUser.text = "Not yet Added" }
                if(skill == ""){ binding.tvSkillUser.text = "Not yet Added" }
                if(nama_cv == ""){ binding.tvNamaCV.text = "No file chosen" }
            }

        // get list education
        service.getEdu(id)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listEdu = ArrayList<Education>()
                for (doc in value!!) {
                    var jurusan = doc.getString("field_of_study")
                    var degree = doc.getString("degree")
                    var univ = doc.getString("school")
                    var id_edu = doc.getString("edu")
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
                    listEditEduAdapter.setData(listEdu)
                }
            }

        // get list experience
        service.getExp(id)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.d("Listen failed.")
//                    binding.progressBar.isVisible = false
                    return@addSnapshotListener
                }
                var listExp = ArrayList<Experience>()
                for (doc in value!!) {
                    var title = doc.getString("title")
                    var role = doc.getString("role")
                    var desc = doc.getString("experience_desc")
                    var exp_start_date = doc.getString("experience_start_date")
                    var exp_end_date = doc.getString("experience_end_date")
                    var exp = Experience(title, role, desc, exp_start_date, exp_end_date)
                    listExp.add(exp)
                }
                if (listExp.isEmpty()) {
                    binding.tvNothingExp.isVisible = true
                    binding.rvListExperience.isVisible = false
                } else {
                    binding.tvNothingExp.isVisible = false
                    binding.rvListExperience.isVisible = true
                    listEditExpAdapter.setData(listExp)
                }

                binding.progressBar.isVisible = false
            }
    }

    private fun logout(){
        val isLogin = LoginPref(requireActivity())
        isLogin.logout()
        val i = Intent(requireActivity(), RegisterMahasiswaActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun uploadtoStorage (uri: Uri, nameFile: String?) {
        binding.progressBar.isVisible = true
        val storageRef = storage.reference
        val path: String = "files/" + UUID.randomUUID() + "/" + nameFile
        val filesRef = storageRef.child(path)
        val uploadFile = filesRef.putFile(uri)

        uploadFile
            .addOnFailureListener {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
                binding.progressBar.isVisible = false
            }
            .addOnSuccessListener { taskSnapshot ->
                Toast.makeText(requireActivity(), "Upload Berhasil", Toast.LENGTH_LONG).show()
            }

        uploadFile.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            filesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                filesRef.metadata.addOnSuccessListener { metadata ->
                    nameCv = metadata.name

                    uploadtoFirestore(downloadUri, nameCv)
                }.addOnFailureListener {
                    Timber.e("Gagal mendapatkan metadata")
                    binding.progressBar.isVisible = false
                }
            }
        }
    }

        @SuppressLint("SimpleDateFormat")
        private fun uploadtoFirestore(downloadUri:Uri, nameFile : String?){
            val id_user = LoginPref(requireActivity()).getIdMhs()
            val typeFile : Long = 5

            service.searchUsersById(id_user!!)
                .update("nama_cv", nameFile)
                .addOnSuccessListener {
                    Timber.d("Sukses update nama cv ke firestore")
                }
                .addOnFailureListener { e ->
                    Timber.tag(ContentValues.TAG).w(e, "Gagal update nama cv ke firestore")
                }

            service.searchUsersById(id_user!!)
                .update("nama_cv", nameFile)
                .addOnSuccessListener {
                    Timber.d("Sukses update nama cv ke firestore")
                }
                .addOnFailureListener { e ->
                    Timber.tag(ContentValues.TAG).w(e, "Gagal update nama cv ke firestore")
                }

            service.searchUsersById(id_user!!)
                .update("url_cv", downloadUri)
                .addOnSuccessListener {
                    Timber.d("Sukses update url cv ke firestore")
                }
                .addOnFailureListener { e ->
                    Timber.tag(ContentValues.TAG).w(e, "Gagal update url cv ke firestore")
                }

        }

    @SuppressLint("Range")
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor : Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            uriAvatar = result.data?.data as Uri
            val myFile = uriToFile(uriAvatar!!, requireActivity())
            getAvatar = myFile
            updateFoto()
//            binding.ivProfile.load(getAvatar) {
//                transformations(CircleCropTransformation())
//            }
        }
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun createCustomTempFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(
            "dd-MMM-yyyy",
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun updateFoto(){
        val storageRef = storage.reference
        val path : String = "avatar/"+ UUID.randomUUID()
        val filesRef = storageRef.child(path)
        val uploadFile = filesRef.putFile(uriAvatar!!)

        uploadFile
            .addOnFailureListener {
                Timber.e(it.message)
                Timber.e("Gagal upload avatar ke storage")
            }
            .addOnSuccessListener { taskSnapshot ->
                Timber.e("berhasil upload avatar ke storage")
            }

        uploadFile.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            filesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                //update avatar to firestore & preference
                var id_user = LoginPref(requireActivity()).getIdMhs()
                service.searchUsersById(id_user!!)
                    .update("foto",downloadUri)
                    .addOnSuccessListener {
                        Timber.d("Sukses update avatar ke firestore")
                        binding.ivProfile.load(downloadUri){
                            transformations(CircleCropTransformation())
                        }
                    }
                    .addOnFailureListener { e -> Timber.tag(ContentValues.TAG).w(e, "Gagal update avatar ke firestore") }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val id = LoginPref(requireActivity()).getIdMhs().toString()
        setProfil(id)
    }
}