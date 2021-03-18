package fr.isen.auroux.backlogapp.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import fr.isen.auroux.backlogapp.databinding.ActivityProjectdisplayBinding
import fr.isen.auroux.backlogapp.network.Project
import fr.isen.auroux.backlogapp.network.User
import fr.isen.auroux.backlogapp.network.UserProject

class ProjectsActivity : BaseActivity(),
    ProjectCellClickListener {
    private lateinit var binding: ActivityProjectdisplayBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserProjectsIds: List<String?>
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectdisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference
        auth = Firebase.auth

        getUser()
        getProjectsIds()

        binding.addProject.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getProjects() {
        val projectListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val projects = dataSnapshot.getValue<HashMap<String, Project>>()
                if (projects != null) {
                    val currentUserProjects = projects.values.filter {
                        currentUserProjectsIds.contains(it.id)
                    }
                    Log.d("count", currentUserProjects.count().toString())
                    updateUi(currentUserProjects)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("projects").addValueEventListener(projectListener)
    }


    private fun getUser() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val users = dataSnapshot.getValue<HashMap<String, User>>()
                user = users?.values?.firstOrNull {
                    it.email == auth.currentUser?.email
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").addListenerForSingleValueEvent(userListener)
    }

    private fun getProjectsIds() {
        val projectListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val userProject = dataSnapshot.getValue<HashMap<String, UserProject>>()
                if (userProject != null) {
                    currentUserProjectsIds = userProject.values.filter {
                        it.userId == user?.id
                    }.map {
                        it.projectId
                    }
                    getProjects()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("user-project").addValueEventListener(projectListener)
    }

    private fun updateUi(projects: List<Project>) {
        val adapter = ProjectAdapter(
            projects,
            this
        )
        binding.listofprojects.layoutManager = LinearLayoutManager(this)
        binding.listofprojects.adapter = adapter
    }

    override fun onClickListener(project: Project) {
        val intent = Intent(this, ProjectBacklogActivity::class.java).apply {
            putExtra("PROJECT", project)
        }

        startActivity(intent)
    }
}