package fr.isen.mahdi.socialnetwork.posts

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import fr.isen.mahdi.socialnetwork.databinding.PostCellBinding
import fr.isen.mahdi.socialnetwork.network.Like
import fr.isen.mahdi.socialnetwork.network.Post
import java.util.*


class PostAdapter(
    private val posts: List<Post>,
    private val postCellClickListener: PostCellClickListener,
    private val database: DatabaseReference): RecyclerView.Adapter<PostAdapter.PostsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(PostCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.postTitle.text = posts[position].title
        holder.postDescription.text = posts[position].body
        posts[position].imageUrl?.let {
            Picasso.get().load(posts[position].imageUrl).into(holder.postImage)
        }
        holder.btnLike.setOnClickListener {
            posts[position].id?.let { it1 -> postCellClickListener.onLikeClickListener(it1) }
        }
        holder.btnComment.setOnClickListener {
            postCellClickListener.onClickListener()
        }
        val likesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var counter = 0
                // Get Post object and use the values to update the UI
                val likes = dataSnapshot.getValue<HashMap<String, Like>>()
                if (likes != null) {
                    Log.d("likes", likes.count().toString())
                }
                likes?.values?.forEach {
                    if (it.postId == posts[position].id) {
                        counter++
                    }
                }
                holder.likesCount.text = "$counter Likes"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("likes").addValueEventListener(likesListener)
    }

    override fun getItemCount(): Int {
        return posts.count()
    }

    class PostsViewHolder(postCellBinding: PostCellBinding): RecyclerView.ViewHolder(postCellBinding.root) {
        val postTitle: TextView = postCellBinding.postTitle
        val postDescription: TextView = postCellBinding.postDescription
        val postImage: ImageView = postCellBinding.postImage
        val btnLike: ImageButton = postCellBinding.btnLike
        val btnComment: ImageButton = postCellBinding.btnComment
        val likesCount: TextView = postCellBinding.likesCount
    }
}