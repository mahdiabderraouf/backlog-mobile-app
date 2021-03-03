package fr.isen.auroux.backlogapp.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.databinding.ActivityReadAndUpdateDataBinding
import fr.isen.auroux.backlogapp.network.User

class ReadAndUpdateDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadAndUpdateDataBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadAndUpdateDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProfileData()

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.btnSave.setOnClickListener {
            if(verifyInformations()) {
                changeProfileData(it.id.toString(), firstname = toString(), lastname = toString(), username = toString(), email = toString())
            }
        }
    }

    private fun verifyInformations(): Boolean {
        return (binding.email.text?.isNotEmpty() == true)
    }

    private fun showProfileData() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val users = dataSnapshot.getValue<HashMap<String, User>>()
                val user = users?.values?.firstOrNull {
                    it.id == auth.currentUser?.uid
                }
                /**
                 * Todo: Save user to cache
                 */
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").setValue(userListener)
    }

    private fun changeProfileData(id: String, firstname: String, lastname: String, username: String, email: String) {
        val key = database.child("users").push().key
        if (key == null) {
            Log.w("firebase", "Couldn't get push key for users")
            return
        }

        val user = User(id, firstname, lastname, username, email)
        val postProfileData = user.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/users/$key" to postProfileData,
            "/user-users/$id/$key" to postProfileData
        )
        database.updateChildren(childUpdates)
    }
}
