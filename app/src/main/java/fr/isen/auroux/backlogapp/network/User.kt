package fr.isen.auroux.backlogapp.network

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val username: String? = null, val email: String? = null, val  lastname: String? = null, val firstname: String? = null, val id: String? =null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}