package fr.isen.auroux.backlogapp.network

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task(
   val id: String? = null,
   val title: String? = null,
   val description: String? = null,
   val status: Int? = null,
   val projectId: String? = null
) {}