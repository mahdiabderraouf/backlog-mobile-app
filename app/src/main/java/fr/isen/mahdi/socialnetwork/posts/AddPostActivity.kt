package fr.isen.mahdi.socialnetwork.posts

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.mahdi.socialnetwork.BaseActivity
import fr.isen.mahdi.socialnetwork.databinding.ActivityAddPostBinding
import fr.isen.mahdi.socialnetwork.network.Post
import java.util.*


class AddPostActivity : BaseActivity() {
    private lateinit var binding:ActivityAddPostBinding
    private lateinit var database: DatabaseReference
    private lateinit var filepath : Uri
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database("https://social-network-cb966-default-rtdb.europe-west1.firebasedatabase.app/").reference
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.imageView.setOnClickListener {
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
            addPost()
        }
    }

    private fun addPost() {
        val title = binding.titleArticle.text.toString().trim()
        val body = binding.descriptionArticle.text.toString().trim()
        if(title.isNotEmpty() && body.isNotEmpty()) {
            val id = database.push().key
            val post = auth.currentUser?.uid?.let {
                Post(id = id, userId = it, title =title, body =body, imageUrl = filepath.toString())
            }
            if (id != null) {
                database.child("posts").child(id).setValue(post)
                Toast.makeText(applicationContext,"Your post has been published", Toast.LENGTH_LONG).show()
            }
            val intent = Intent(this, PostsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext,"Please fill all fields.", Toast.LENGTH_LONG).show()
        }
    }

    private fun chooseImage() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun uploadImage() {
        val imageName = UUID.randomUUID().toString()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("image Uploading")
        progressDialog.show()

        val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/$imageName")
        imageRef.putFile(filepath)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Image Uploaded", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {  p0 ->
                progressDialog.dismiss()
                Toast.makeText(this, p0.message, Toast.LENGTH_LONG).show()
            }
            .addOnProgressListener { p0 ->
                val progress : Double = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                progressDialog.setMessage("Uploaded ${progress.toInt()}%")
            }
        imageRef.child("users/me/profile.png").downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
            filepath = it
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 10
        private const val PERMISSION_CODE = 11
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
            binding.imageView.setImageURI(filepath)
        }
    }
}