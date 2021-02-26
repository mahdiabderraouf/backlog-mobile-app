package fr.isen.auroux.backlogapp.project

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.auroux.backlogapp.BaseActivity
import fr.isen.auroux.backlogapp.databinding.ActivityAddProjectBinding
import fr.isen.auroux.backlogapp.network.Project
import java.util.*


class AddProjectActivity : BaseActivity() {
    private lateinit var binding:ActivityAddProjectBinding
    private lateinit var database: DatabaseReference
     lateinit var filepath : Uri

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
        binding = ActivityAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


        binding.chooseImage.setOnClickListener {
          //check runtime permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else {
                    chooseImage()
                }
            } else {
                chooseImage()
            }
        }

        binding.imgPickBtn.setOnClickListener {
            uploadImage()
            addProject()
        }
    }
    private fun chooseImage() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    private fun uploadImage() {

        if(filepath != null) {
            val imageName = UUID.randomUUID().toString()
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("image Uploading")
            progressDialog.show()

            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/$imageName")
            imageRef.putFile(filepath)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                   // Toast.makeText(this,"Image Uploaded", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {  p0 ->
                    progressDialog.dismiss()
                    Toast.makeText(this, p0.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0 ->
                    val progress : Double = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    progressDialog.setMessage("Uploaded ${progress.toInt()}%")

                }

            val imagepath = imageRef.child("users/me/profile.png")

            imagepath.downloadUrl.addOnSuccessListener {
                filepath = it
            }.addOnFailureListener {
                // Handle any errors
            }
        }
        else
        {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }


        private fun addProject() {
        val key = database.child("projects").push().key
        val id = auth.currentUser?.uid
        val title = binding.titleProject.text.toString().trim()

            val project = Project(
                id = id,
                title = title,
                imagePath = filepath.toString()
            )
            if (title.isNotEmpty()) {
                key?.let { database.child("projects").child(it).setValue(project) }
                Toast.makeText(applicationContext,"Your project has been saved", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(applicationContext, "Please fill the field.", Toast.LENGTH_LONG).show()
            }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults [0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup granted
                    chooseImage()
                }
                else {
                    //permission from popup denied
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            filepath = data?.data!!
            binding.chooseImage.setImageURI(filepath)
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 100
        private const val PERMISSION_CODE = 111
    }
}