package com.pmsi.lamaruin.ui.recruiter.profile

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.WelcomeActivity
import com.pmsi.lamaruin.data.LoginPref
import com.pmsi.lamaruin.data.remote.FirestoreService
import com.pmsi.lamaruin.databinding.FragmentProfileRecrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileRecrFragment : Fragment() {

    @Inject
    lateinit var service: FirestoreService
    val storage = Firebase.storage("gs://lamaruin-92998.appspot.com")
    private var uri: Uri? = null
    private var getAvatar: File? = null
    private var uriAvatar: Uri? = null

    private lateinit var binding: FragmentProfileRecrBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileRecrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnInputJob.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_profileRecFragment_to_inputJobActivity)
        )
        binding.button7.setOnClickListener { logout() }
        binding.btnEdtProfile.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_profileRecFragment_to_editProfileRecrActivity)
        )
        var id_user = LoginPref(requireActivity()).getIdMhs().toString()
        setProfile(id_user)

        binding.changeAvatar.setOnClickListener {startGallery()}

    }

    private fun setProfile(id : String){

        service.searchRecrById(id)
            .get()
            .addOnSuccessListener {
                var nama = it.getString("name")
                var foto = it.getString("foto")
                var idAccount = it.getString("id_recruiter")
                var companyName = it.getString("company_name")
                var companyProfile = it.getString("company_profile")
                var phone = it.getString("no_hp")


                binding.ivProfile.load(foto){
                    transformations(CircleCropTransformation())
                }
                binding.apply{
                    tvNamaRecr.text = nama
                    tvIdAcc.text = idAccount
                    tvCompanyNamee.text = companyName
                    tvCompanyProf.text = companyProfile
                    tvPhoneNumberRec.text = phone
                }

                if(companyName == ""){ binding.tvCompanyNamee.text = "Not yet Added" }
                if(companyProfile == ""){ binding.tvCompanyProf.text = "Not yet Added" }
                if(phone == ""){ binding.tvPhoneNumberRec.text = "Not yet Added" }

            }
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

    fun createCustomTempFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(
            "dd-MMM-yyyy",
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
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
                service.searchRecrById(id_user!!)
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

    private fun logout(){
        val isLogin = LoginPref(requireActivity())
        isLogin.logout()
        val i = Intent(requireActivity(), WelcomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }
}