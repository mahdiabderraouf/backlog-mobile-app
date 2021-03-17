package fr.isen.auroux.backlogapp.network

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Project(
    val id: String? = null,
    val title: String? = null,
    val imagePath: String? = null,
    val userId: String? = null
): Serializable {}
