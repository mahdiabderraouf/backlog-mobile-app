package fr.isen.auroux.backlogapp

import android.content.Intent
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.auroux.backlogapp.profile.ReadAndUpdateDataActivity

open class BaseActivity: AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuView = menu.findItem(R.id.logout).actionView

        menuView.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Firebase.auth.currentUser
            val profile = Intent(this, ReadAndUpdateDataActivity::class.java)
            startActivity(profile)
        }
        return true
    }
}