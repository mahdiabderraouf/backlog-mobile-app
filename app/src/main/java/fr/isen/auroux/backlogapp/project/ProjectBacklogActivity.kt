package fr.isen.auroux.backlogapp.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.databinding.ActivityProjectBacklogBinding
import fr.isen.auroux.backlogapp.network.Task

class ProjectBacklogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectBacklogBinding
    private lateinit var database: DatabaseReference
    private var tasks: List<Task>? = null
    private lateinit var projectId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBacklogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        actionBar?.title = "Current project"

        database = Firebase.database.reference

        // Get project id from intent extra
        projectId = "-MTqJD878AnZhiFMx718"

        getProjectTasks(projectId)

        setClickListeners()
    }

    private fun getProjectTasks(projectId: String) {
        val tasksRef = database.child("tasks")
        val tasksListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                 tasks = dataSnapshot.getValue<HashMap<String, Task>>()?.values?.filter {
                    it.projectId == projectId
                }
                updateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        tasksRef.addValueEventListener(tasksListener)
    }

    private fun updateUI() {
        tasks?.let {
            val todoTasks = it.filter { task ->
                task.status == 1
            }
            binding.todoCount.text = todoTasks.count().toString()
            val todoTasksAdapter = TaskAdapter(todoTasks)
            binding.todoTasksList.layoutManager = LinearLayoutManager(this)
            binding.todoTasksList.adapter = todoTasksAdapter

            val doingTasks = it.filter { task ->
                task.status == 2
            }
            binding.doingCount.text = doingTasks.count().toString()
            val doingTasksAdapter = TaskAdapter(doingTasks)
            binding.doingTasksList.layoutManager = LinearLayoutManager(this)
            binding.doingTasksList.adapter = doingTasksAdapter

            val doneTasks = it.filter { task ->
                task.status == 3
            }
            binding.doneCount.text = doneTasks.count().toString()
            val doneTaskAdapter = TaskAdapter(doneTasks)
            binding.doneTasksList.layoutManager = LinearLayoutManager(this)
            binding.doneTasksList.adapter = doneTaskAdapter
        }
    }

    private fun isEmpty(element: TextInputEditText): Boolean {
        element.text?.let {
            return if (it.isEmpty()) {
                Toast.makeText(this, "Please fill form.", Toast.LENGTH_SHORT).show()
                true
            } else
                false
        }
        return false
    }

    private fun setClickListeners() {
        binding.btnShowAddForm.setOnClickListener {
            binding.textInputLayout1.isGone = false
            binding.cancelBtn1.isGone = false
            binding.addBtn1.isGone = false
            binding.btnShowAddForm.isGone = true
        }

        binding.cancelBtn1.setOnClickListener {
            binding.cancelBtn1.isGone = true
            binding.textInputLayout1.isGone = true
            binding.addBtn1.isGone = true
            binding.btnShowAddForm.isGone = false
        }

        binding.btnShowAddFormDoing.setOnClickListener {
            binding.textInputLayout2.isGone = false
            binding.cancelBtn2.isGone = false
            binding.addBtn2.isGone = false
            binding.btnShowAddFormDoing.isGone = true
        }

        binding.cancelBtn2.setOnClickListener {
            binding.cancelBtn2.isGone = true
            binding.textInputLayout2.isGone = true
            binding.addBtn2.isGone = true
            binding.btnShowAddFormDoing.isGone = false
        }

        binding.btnShowAddFormDone.setOnClickListener {
            binding.textInputLayout3.isGone = false
            binding.cancelBtn3.isGone = false
            binding.addBtn3.isGone = false
            binding.btnShowAddFormDone.isGone = true
        }

        binding.cancelBtn3.setOnClickListener {
            binding.cancelBtn3.isGone = true
            binding.textInputLayout3.isGone = true
            binding.addBtn3.isGone = true
            binding.btnShowAddFormDone.isGone = false
        }

        binding.addBtn1.setOnClickListener {
            if (!isEmpty(binding.todoTextInput)) {
                addTask(binding.todoTextInput.text.toString(), 1)
                binding.cancelBtn1.isGone = true
                binding.textInputLayout1.isGone = true
                binding.addBtn1.isGone = true
                binding.btnShowAddForm.isGone = false
            }
        }

        binding.addBtn2.setOnClickListener {
            if (!isEmpty(binding.doingTextInput)) {
                addTask(binding.doingTextInput.text.toString(), 2)
                binding.cancelBtn2.isGone = true
                binding.textInputLayout2.isGone = true
                binding.addBtn2.isGone = true
                binding.btnShowAddFormDoing.isGone = false
            }
        }

        binding.addBtn3.setOnClickListener {
            if (!isEmpty(binding.doneTextInput)) {
                addTask(binding.doneTextInput.text.toString(), 3)
                binding.cancelBtn3.isGone = true
                binding.textInputLayout3.isGone = true
                binding.addBtn3.isGone = true
                binding.btnShowAddFormDone.isGone = false
            }
        }
    }

    private fun addTask(title: String, status: Int) {
        val key = database.child("tasks").push().key
        val task = Task(key, title, "", status, projectId)
        if (key != null) {
            val addOperation = database.child("tasks").child(key).setValue(task)
            addOperation.addOnSuccessListener {
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
            }
            addOperation.addOnFailureListener {
                Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}