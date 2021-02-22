package fr.isen.auroux.backlogapp.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.auroux.backlogapp.databinding.ProjectCellBinding
import fr.isen.auroux.backlogapp.network.Project


class ProjectAdapter(
    private val projects: List<Project>,
    private val projectCellClickListener: ProjectCellClickListener
): RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        return ProjectViewHolder(
            ProjectCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.projectTitle.text = projects[position].title
        projects[position].imagePath?.let {
            Picasso.get().load(projects[position].imagePath).into(holder.projectImage)
        }

        projects[position].let { project ->
            holder.projectCardView.setOnClickListener {
                projectCellClickListener.onClickListener(project)
            }
        }
    }

    override fun getItemCount(): Int {
        return projects.count()
    }

    class ProjectViewHolder(projectCellBinding: ProjectCellBinding): RecyclerView.ViewHolder(projectCellBinding.root) {
        val projectTitle: TextView = projectCellBinding.projectName
        val projectImage: ImageView = projectCellBinding.projectImage
        val projectCardView: CardView = projectCellBinding.projectCardView
    }
}