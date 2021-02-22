package fr.isen.auroux.backlogapp.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.auroux.backlogapp.databinding.ProjectCellBinding
import fr.isen.auroux.backlogapp.network.Project


class ProjectAdapter(
    private val projects: List<Project>): RecyclerView.Adapter<ProjectAdapter.PostsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(
            ProjectCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.projectTitle.text = projects[position].title
        projects[position].imagePath?.let {
            Picasso.get().load(projects[position].imagePath).into(holder.projectImage)
        }
    }

    override fun getItemCount(): Int {
        return projects.count()
    }

    class PostsViewHolder(postCellBinding: ProjectCellBinding): RecyclerView.ViewHolder(postCellBinding.root) {
        val projectTitle: TextView = postCellBinding.projectName
        val projectImage: ImageView = postCellBinding.projectImage
    }
}