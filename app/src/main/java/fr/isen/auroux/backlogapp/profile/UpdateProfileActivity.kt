package fr.isen.auroux.backlogapp.profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.BaseActivity
import fr.isen.auroux.backlogapp.databinding.ActivityUpdateProfileBinding
import fr.isen.auroux.backlogapp.network.User

class UpdateProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = Firebase.database.reference

        getUser()

        binding.btnSave.setOnClickListener {
            if (verifyInformations()) {
                updateProfile()
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyInformations(): Boolean {
        return binding.email.text?.isNotEmpty() == true &&
                binding.firstname.text?.isNotEmpty() == true &&
                binding.lastname.text?.isNotEmpty() == true &&
                binding.username.text?.isNotEmpty() == true
    }

    private fun getUser() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val users = dataSnapshot.getValue<HashMap<String, User>>()
                user = users?.values?.firstOrNull {
                    it.email == auth.currentUser?.email
                }
                user?.let { updateUI(it) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").addValueEventListener(userListener)
    }

    private fun updateUI(user: User) {
        binding.email.setText(user.email)
        binding.firstname.setText(user.firstname)
        binding.lastname.setText(user.lastname)
        binding.username.setText(user.username)
    }

    private fun updateProfile() {
        user?.let {
            val newUser = User(
                binding.username.text.toString(),
                binding.email.text.toString(),
                binding.lastname.text.toString(),
                binding.firstname.text.toString(),
                it.id
            )
            val userData = it.toMap()

            val childUpdates = hashMapOf<String, Any>(
                "/users/${it.id}" to userData
            )
            database.updateChildren(childUpdates).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT)
                    .show()

            }
        }

    }
}
