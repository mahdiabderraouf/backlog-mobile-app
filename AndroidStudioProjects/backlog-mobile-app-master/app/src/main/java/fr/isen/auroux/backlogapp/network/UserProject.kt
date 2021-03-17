package fr.isen.auroux.backlogapp.network

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class UserProject(
    val userId: String? = null,
    val projectId: String? = null
): Serializable {}
