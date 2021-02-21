package fr.isen.auroux.backlogapp.project

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import fr.isen.auroux.backlogapp.BaseActivity
import fr.isen.auroux.backlogapp.R
import fr.isen.auroux.backlogapp.databinding.ActivityAddProjectBinding


class AddProjectActivity : BaseActivity() {
    private lateinit var binding:ActivityAddProjectBinding
    private lateinit var filepath : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    }

    private fun chooseImage() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
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
        private const val IMAGE_PICK_CODE = 10
        private const val PERMISSION_CODE = 11
    }

}