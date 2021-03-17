package fr.isen.auroux.backlogapp.task

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.auroux.backlogapp.databinding.TaskCellBinding
import fr.isen.auroux.backlogapp.network.Task

class TaskAdapter(
    private val tasks: List<Task>,
    private val context: Context
) : RecyclerView.Adapter<TaskAdapter.TasksViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            TaskCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.textView.text = tasks[position].title
        holder.cardView.setOnLongClickListener(tasks[position].id?.let { onLongClickListener(it) })
        holder.cardView.setOnDragListener(onDragListener())
    }

    override fun getItemCount(): Int {
        return tasks.count()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onLongClickListener(id: String): View.OnLongClickListener {
        return View.OnLongClickListener { v: View ->
            val item = ClipData.Item(id)
            val dragData = ClipData(
                "",
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            val myShadow = TaskDragShadowBuilder(v)

            // Starts the drag
            v.startDragAndDrop(
                dragData,   // the data to be dragged
                myShadow,   // the drag shadow builder
                null,       // no need to use local data
                0           // flags (not currently used, set to 0)
            )
        }
    }

    private fun onDragListener(): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if (!event.result) {
                        Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }
        }
    }

    class TasksViewHolder(taskCellBinding: TaskCellBinding) :
        RecyclerView.ViewHolder(taskCellBinding.root) {
        val textView: TextView = taskCellBinding.taskTitle
        val cardView: CardView = taskCellBinding.cardView
    }
}