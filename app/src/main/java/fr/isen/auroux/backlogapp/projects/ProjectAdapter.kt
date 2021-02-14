package fr.isen.auroux.backlogapp.projects

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import fr.isen.auroux.backlogapp.databinding.ProjectCellBinding
import fr.isen.auroux.backlogapp.network.Project


class ProjectAdapter(
    private val listofprojects: List<Project>,
    private val postCellClickListener: PostCellClickListener,
    private val database: DatabaseReference): RecyclerView.Adapter<ProjectAdapter.PostsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(ProjectCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.projectTitle.text = listofprojects[position].title
        holder.projectDescription.text = listofprojects[position].description
        listofprojects[position].imageUrl?.let {
            Picasso.get().load(listofprojects[position].imageUrl).into(holder.projectImage)
        }
    }

    override fun getItemCount(): Int {
        return listofprojects.count()
    }

    class PostsViewHolder(postCellBinding: ProjectCellBinding): RecyclerView.ViewHolder(postCellBinding.root) {
        val projectTitle: TextView = postCellBinding.projectName
        val projectDescription: TextView = postCellBinding.projectDescription
        val projectImage: ImageView = postCellBinding.projectImage
    }
}