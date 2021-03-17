package fr.isen.auroux.backlogapp.project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import fr.isen.auroux.backlogapp.network.UserProject

class AddCollaboratorActivity : BaseActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityAddCollaboratorBinding
    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCollaboratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database.reference

        project = intent.getSerializableExtra("PROJECT") as Project

        binding.CollButton.setOnClickListener {

            if (verifyInformations()){
                getEmail()
            }
            else{
                Toast.makeText(this, "Veuillez rentrer un Email", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun addCollaborator(user: User){
        val id = database.push().key
        val userProject = UserProject(
            userId = user.id,
            projectId = project.id
        )
        if (id != null) {
            database.child("user-project").child(id).setValue(userProject)
            Toast.makeText(
                applicationContext,
                "Le collaborator a bien été ajouté au projet",
                Toast.LENGTH_LONG
            ).show()
            binding.CollEmail.setText("")
        }

    }
    private fun getEmail() {
        val collaboratorListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue<HashMap<String, User>>()
                if (users != null) {
                    val user = users.values.firstOrNull {
                        it.email == binding.CollEmail.text.toString()
                    }
                    if (user != null){
                        addCollaborator(user)
                    }
                    else{
                        Toast.makeText(this@AddCollaboratorActivity, "Cette Email n'existe pas", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }

        database.child("users").addValueEventListener(collaboratorListener)
    }
    private fun verifyInformations(): Boolean {
        return (binding.CollEmail.text?.isNotEmpty() == true)
    }
}