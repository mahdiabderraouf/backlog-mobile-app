package fr.isen.auroux.backlogapp.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.databinding.ActivityProjectBacklogBinding
import fr.isen.auroux.backlogapp.network.Project
import fr.isen.auroux.backlogapp.network.Task

class ProjectBacklogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectBacklogBinding
    private lateinit var database: DatabaseReference
    private var tasks: List<Task>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBacklogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference

        // Get project id from intent extra
        val projectId = "-MTqJD878AnZhiFMx718"

        getProjectTasks(projectId)
    }

    private fun getProjectTasks(projectId: String) {
        val tasksRef = database.child("tasks")
        val tasksListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                 tasks = dataSnapshot.getValue<HashMap<String, Task>>()?.values?.filter {
                    it.projectId == projectId
                }
                createAdapters()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        tasksRef.addValueEventListener(tasksListener)
    }

    private fun createAdapters() {
        tasks?.let {
            val todoTasks = it.filter { task ->
                task.status == 1
            }
            val todoTasksAdapter = TaskAdapter(todoTasks)
            binding.todoTasksList.layoutManager = LinearLayoutManager(this)
            binding.todoTasksList.adapter = todoTasksAdapter
        }
    }
}