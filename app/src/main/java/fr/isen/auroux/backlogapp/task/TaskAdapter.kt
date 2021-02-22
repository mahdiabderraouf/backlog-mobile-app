package fr.isen.auroux.backlogapp.task

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.auroux.backlogapp.databinding.TaskCellBinding
import fr.isen.auroux.backlogapp.network.Task
import java.security.AccessController.getContext


class TaskAdapter (
    private val tasks: List<Task>,
    private val context: Context
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.textView.text = tasks[position].title
        holder.cardView.setOnLongClickListener { v: View ->
            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag
            val item = ClipData.Item(v.tag as? CharSequence)

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            val dragData = ClipData(
                v.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item)

            // Instantiates the drag shadow builder.
            val myShadow = TaskDragShadowBuilder(v)

            // Starts the drag
            v.startDrag(
                dragData,   // the data to be dragged
                myShadow,   // the drag shadow builder
                null,       // no need to use local data
                0           // flags (not currently used, set to 0)
            )
        }
        holder.cardView.setOnDragListener { v, event ->
            // Handles each of the expected events
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        (v as? ImageView)?.setColorFilter(Color.BLUE)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        // returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    (v as? ImageView)?.setColorFilter(Color.GREEN)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    // Ignore event

                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    (v as? ImageView)?.setColorFilter(Color.BLUE)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Turns off any color tinting
                    (v as? ImageView)?.clearColorFilter()

                    // Invalidates the view to force a redraw
                    v.invalidate()

                    // Does a getResult(), and displays what happened.
                    when(event.result) {
                        true ->
                            Toast.makeText(context, "The drop was handled.", Toast.LENGTH_SHORT)
                        else ->
                            Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_SHORT)
                    }.show()

                    // returns true; the value is ignored.
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

    override fun getItemCount(): Int {
        return tasks.count()
    }

    class TasksViewHolder(taskCellBinding: TaskCellBinding) : RecyclerView.ViewHolder(taskCellBinding.root) {
        val textView: TextView = taskCellBinding.taskTitle
        val cardView: CardView = taskCellBinding.cardView
    }
}