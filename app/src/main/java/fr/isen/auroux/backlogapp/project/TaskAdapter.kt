package fr.isen.auroux.backlogapp.project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.auroux.backlogapp.databinding.TaskCellBinding
import fr.isen.auroux.backlogapp.network.Task

class TaskAdapter (
    private val tasks: List<Task>
    ): RecyclerView.Adapter<TaskAdapter.TasksViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            TaskCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.textView.text = tasks[position].title
        holder.cardView.setOnDragListener { v, event -> event }
    }

    override fun getItemCount(): Int {
        return tasks.count()
    }

    class TasksViewHolder(taskCellBinding: TaskCellBinding) : RecyclerView.ViewHolder(taskCellBinding.root) {
        val textView: TextView = taskCellBinding.taskTitle
        val cardView: CardView = taskCellBinding.cardView
    }
}