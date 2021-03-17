package fr.isen.auroux.backlogapp.project

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import fr.isen.auroux.backlogapp.BaseActivity
import fr.isen.auroux.backlogapp.R
import fr.isen.auroux.backlogapp.databinding.ActivityProjectBacklogBinding
import fr.isen.auroux.backlogapp.network.Project
import fr.isen.auroux.backlogapp.network.Task
import fr.isen.auroux.backlogapp.task.TaskAdapter

class ProjectBacklogActivity : BaseActivity() {
    private lateinit var binding: ActivityProjectBacklogBinding
    private lateinit var database: DatabaseReference
    private var tasks: List<Task>? = null
    private lateinit var project: Project

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.delete_project).isVisible = true
        menu.findItem(R.id.invite_user).isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.delete_project -> {
                deleteProject()
                true
            }
            R.id.invite_user -> {
                val intent = Intent(this, AddCollaboratorActivity::class.java).apply {
                    putExtra("PROJECT", project)
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBacklogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getSerializableExtra("PROJECT") as Project

        val actionBar = supportActionBar
        actionBar?.title = project.title

        database = Firebase.database.reference

        project.id?.let { getProjectTasks(it) }

        setClickListeners()
        setDropListeners()
    }

    private fun getProjectTasks(projectId: String) {
        val tasksRef = database.child("tasks")
        val tasksListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tasks = dataSnapshot.getValue<HashMap<String, Task>>()?.values?.filter {
                    it.projectId == projectId
                }
                updateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("firebase", "Failed to load project.")
            }
        }
        tasksRef.addValueEventListener(tasksListener)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        tasks?.let { it ->
            val todoTasks = it.filter { task ->
                task.status == 1
            }
            binding.todoCount.text = todoTasks.count().toString()
            val todoTasksAdapter =
                TaskAdapter(todoTasks, this)
            binding.todoTasksList.layoutManager = LinearLayoutManager(this)
            binding.todoTasksList.adapter = todoTasksAdapter

            val doingTasks = it.filter { task ->
                task.status == 2
            }
            binding.doingCount.text = doingTasks.count().toString()
            val doingTasksAdapter =
                TaskAdapter(doingTasks, this)
            binding.doingTasksList.layoutManager = LinearLayoutManager(this)
            binding.doingTasksList.adapter = doingTasksAdapter

            val doneTasks = it.filter { task ->
                task.status == 3
            }
            binding.doneCount.text = doneTasks.count().toString()
            val doneTaskAdapter =
                TaskAdapter(doneTasks, this)
            binding.doneTasksList.layoutManager = LinearLayoutManager(this)
            binding.doneTasksList.adapter = doneTaskAdapter
            tasks?.let { it2 ->
                if (it2.count() > 0) {
                    val percentage = doneTasks.count() * 100 / it2.count()
                    binding.progressPercentage.text = "$percentage %"
                    binding.progressBar.setProgress(percentage, true)
                } else {
                    binding.progressPercentage.text = "0 %"
                    binding.progressBar.setProgress(0, true)
                }
            }
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
            toggleForm("todo")
        }

        binding.cancelBtn1.setOnClickListener {
            toggleForm("todo")
        }

        binding.btnShowAddFormDoing.setOnClickListener {
            toggleForm("doing")
        }

        binding.cancelBtn2.setOnClickListener {
            toggleForm("doing")
        }

        binding.btnShowAddFormDone.setOnClickListener {
            toggleForm("done")
        }

        binding.cancelBtn3.setOnClickListener {
            toggleForm("done")
        }

        binding.addBtn1.setOnClickListener {
            if (!isEmpty(binding.todoTextInput)) {
                addTask(binding.todoTextInput.text.toString(), 1)
                binding.todoTextInput.text = null
                toggleForm("todo")
            }
        }

        binding.addBtn2.setOnClickListener {
            if (!isEmpty(binding.doingTextInput)) {
                addTask(binding.doingTextInput.text.toString(), 2)
                binding.doingTextInput.text = null
                toggleForm("doing")
            }
        }

        binding.addBtn3.setOnClickListener {
            if (!isEmpty(binding.doneTextInput)) {
                addTask(binding.doneTextInput.text.toString(), 3)
                binding.doneTextInput.text = null
                toggleForm("done")
            }
        }
    }

    private fun addTask(title: String, status: Int) {
        val key = database.child("tasks").push().key
        val task = Task(key, title, "", status, project.id)
        if (key != null) {
            database.child("tasks").child(key).setValue(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Something went wrong, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setDropListeners() {
        binding.mainContainer.setOnDragListener(onDragToEdgesListener())
        binding.todoCard.setOnDragListener(onDropListener(1))
        binding.doingCard.setOnDragListener(onDropListener(2))
        binding.doneCard.setOnDragListener(onDropListener(3))
    }

    private fun onDropListener(status: Int): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val taskId: String = event.clipData.getItemAt(0).text.toString()
                    val task = tasks?.firstOrNull {
                        it.id == taskId
                    }
                    if (task !== null) {
                        task.status = status
                        val taskValues = task.toMap()
                        database.updateChildren(
                            hashMapOf<String, Any>(
                                "/tasks/$taskId" to taskValues
                            )
                        )
                    }
                    true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e("drop error", "Unknown action type received by OnDragListener.")
                    false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun onDragToEdgesListener(): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // When the view is dragged to the edge of the screen, scroll the horizontal layout
                    val outMetrics = DisplayMetrics()
                    this.display?.getRealMetrics(outMetrics)

                    if (event.x > outMetrics.widthPixels - 400 && event.x < outMetrics.widthPixels) {
                        binding.horizontalScroll.smoothScrollBy(60, 0)
                    }

                    if (event.x < 400) {
                        binding.horizontalScroll.smoothScrollBy(-60, 0)
                    }

                    if (event.y > outMetrics.heightPixels - 400 && event.y < outMetrics.heightPixels) {
                        binding.horizontalScroll.smoothScrollBy(0, 60)
                    }

                    if (event.y < 400) {
                        binding.horizontalScroll.smoothScrollBy(0, 660)
                    }

                    true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e("drop error", "Unknown action type received by OnDragListener.")
                    false
                }
            }
        }
    }

    private fun toggleForm(form: String) {
        when (form) {
            "todo" -> {
                binding.textInputLayout1.isGone = !binding.textInputLayout1.isGone
                binding.cancelBtn1.isGone = !binding.cancelBtn1.isGone
                binding.addBtn1.isGone = !binding.addBtn1.isGone
                binding.btnShowAddForm.isGone = !binding.btnShowAddForm.isGone
            }
            "doing" -> {
                binding.textInputLayout2.isGone = !binding.textInputLayout2.isGone
                binding.cancelBtn2.isGone = !binding.cancelBtn2.isGone
                binding.addBtn2.isGone = !binding.addBtn2.isGone
                binding.btnShowAddFormDoing.isGone = !binding.btnShowAddFormDoing.isGone
            }
            "done" -> {
                binding.textInputLayout3.isGone = !binding.textInputLayout3.isGone
                binding.cancelBtn3.isGone = !binding.cancelBtn3.isGone
                binding.addBtn3.isGone = !binding.addBtn3.isGone
                binding.btnShowAddFormDone.isGone = !binding.btnShowAddFormDone.isGone
            }
            else -> {
                Log.e("error", "Invalid form.")
            }
        }
    }

    private fun deleteProject() {
        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }
        builder?.setMessage("Are you sure ?")
            ?.setTitle("Confirm delete")
            ?.setPositiveButton(
                "Confirm"
            ) { _, _ ->
                project.id?.let { database.child("projects").child(it).removeValue() }
                val intent = Intent(this, ProjectsActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Project deleted successfully.", Toast.LENGTH_LONG).show()
            }?.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }
}