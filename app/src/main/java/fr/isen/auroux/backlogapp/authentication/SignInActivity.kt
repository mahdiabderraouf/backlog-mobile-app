package fr.isen.auroux.backlogapp.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.databinding.ActivitySigninBinding
import fr.isen.auroux.backlogapp.network.User
import fr.isen.auroux.backlogapp.project.AddProjectActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.btnSignIn.setOnClickListener {
            if(verifyInformations()) {
                 login()

            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show()
            }
        }
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                   /* val intent = Intent(this, AddProjectActivity::class.java)
                    startActivity(intent)*/
                    getUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("firebase", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verifyInformations(): Boolean {
        return (binding.email.text?.isNotEmpty() == true &&
                binding.password.text?.count() ?: 0 >= 6)
    }

    private fun getUser() {
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
        database.child("users").addListenerForSingleValueEvent(userListener)
    }
}