package fr.isen.auroux.backlogapp.projects

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
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
import fr.isen.auroux.backlogapp.R
import fr.isen.auroux.backlogapp.databinding.ActivityProjectdisplayBinding
import fr.isen.auroux.backlogapp.network.Project


class ProjectsActivity : BaseActivity(), PostCellClickListener {
    private lateinit var binding: ActivityProjectdisplayBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var finished: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectdisplayBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        database = Firebase.database("").reference
        auth = Firebase.auth

        getPosts(database.child("projects"))

        binding.addProject.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            startActivity(intent)
        }

        val finished = findViewById(R.id.checkBox) as CheckBox
        val result = StringBuilder()
        if (finished.isChecked) {
                result.append("project finished/n")
        }
    }

    private fun getPosts(postsReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val listofprojects = dataSnapshot.getValue<HashMap<String, Project>>()
                if (listofprojects!= null) {
                    updateUi(ArrayList(listofprojects.values))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        postsReference.addValueEventListener(postListener)
    }

    private fun updateUi(projects: List<Project>) {
        val adapter = ProjectAdapter(projects, this, database)
        binding.listofprojects.layoutManager = LinearLayoutManager(this)
        binding.listofprojects.adapter = adapter
    }

    override fun onLikeClickListener(postId: String) {
        TODO("Not yet implemented")
    }

    override fun onClickListener() {
        // Start detail activity
    }
}