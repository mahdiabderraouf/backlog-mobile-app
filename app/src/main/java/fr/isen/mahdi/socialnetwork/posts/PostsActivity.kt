package fr.isen.mahdi.socialnetwork.posts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.mahdi.socialnetwork.BaseActivity
import fr.isen.mahdi.socialnetwork.databinding.ActivityPostsBinding
import fr.isen.mahdi.socialnetwork.network.Like
import fr.isen.mahdi.socialnetwork.network.Post

class PostsActivity : BaseActivity(), PostCellClickListener {
    private lateinit var binding: ActivityPostsBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database("https://social-network-cb966-default-rtdb.europe-west1.firebasedatabase.app/").reference
        auth = Firebase.auth

        getPosts(database.child("posts"))

        binding.addPost.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getPosts(postsReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val posts = dataSnapshot.getValue<HashMap<String, Post>>()
                if (posts != null) {
                    updateUi(ArrayList(posts.values))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        postsReference.addValueEventListener(postListener)
    }

    private fun updateUi(posts: List<Post>) {
        val adapter = PostAdapter(posts, this, database)
        binding.posts.layoutManager = LinearLayoutManager(this)
        binding.posts.adapter = adapter
    }

    override fun onLikeClickListener(postId: String) {
        val likesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var alreadyLiked = false
                var key = ""
                // Get Post object and use the values to update the UI
                val likes = dataSnapshot.getValue<java.util.HashMap<String, Like>>()
                likes?.forEach {
                    if (it.value.postId == postId && it.value.userId == auth.currentUser?.uid) {
                        alreadyLiked = true
                        key = it.key
                    }
                }
                if (alreadyLiked) {
                    database.child("likes").child(key).removeValue()
                } else {
                    val id = database.push().key
                    val like = Like(postId=postId, userId= auth.currentUser?.uid)
                    if (id != null) {
                        database.child("likes").child(id).setValue(like)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("likes").addListenerForSingleValueEvent(likesListener)
    }

    override fun onClickListener() {
        // Start detail activity
    }
}