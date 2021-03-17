package fr.isen.auroux.backlogapp.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.BaseActivity
import fr.isen.auroux.backlogapp.databinding.ActivityAddCollaboratorBinding
import fr.isen.auroux.backlogapp.network.Project
import fr.isen.auroux.backlogapp.network.User

class AddCollaboratorActivity /* BaseActivity*/ {
   /* private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityAddCollaboratorBinding*/

  CollButton.setOnClickListener
    {
        val emailColl = CollEmail.text.toString()

        Log.w(tag:"AddCollaboratorActivity", msg:"Email is: " + emailColl)
    }

    private fun getEmail(ref: DatabaseReference) {
        val collaboratorListener = object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val collaborator = dataSnapshot.getValue<HashMap<String, Collaborator>>()
        if (collaborator != null) {
            val currentUserProjects = collaborator.values.filter {

            }
            }
        }
    }
    private fun verifyInformations(): Boolean {
        return (binding.Collemail.text?.isNotEmpty() == true )
    }*/
}
