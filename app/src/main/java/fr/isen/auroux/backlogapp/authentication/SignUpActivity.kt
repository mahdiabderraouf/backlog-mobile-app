package fr.isen.auroux.backlogapp.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.databinding.ActivitySignupBinding
import fr.isen.auroux.backlogapp.network.User

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = Firebase.database.reference

        binding.btnSignUp.setOnClickListener {
            if (verifyInformations()) {
                register()
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register() {
        auth.createUserWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("firebase", "createUserWithEmail:success")
                    addUser()

                    //startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("firebase", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUser(){
        val id = database.child("users").push().key
        val user = User(
            id = id,
            username = binding.username.text.toString(),
            lastname = binding.lastname.text.toString(),
            firstname = binding.firstname.text.toString(),
            email = binding.email.text.toString()
        )
        id?.let { database.child("users").child(it).setValue(user) }
    }

    private fun verifyInformations(): Boolean {
        return (binding.email.text?.isNotEmpty() == true &&
                binding.password.text?.count() ?: 0 >= 6 &&
                binding.username.text?.isNotEmpty() == true &&
                binding.lastname.text?.isNotEmpty() == true &&
                binding.firstname.text?.isNotEmpty() == true
                )
    }
}