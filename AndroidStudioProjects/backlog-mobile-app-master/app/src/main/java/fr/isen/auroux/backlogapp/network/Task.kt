package fr.isen.auroux.backlogapp.network

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task(
   val id: String? = null,
   val title: String? = null,
   val description: String? = null,
   var status: Int? = null,
   val projectId: String? = null
) {
   @Exclude
   fun toMap(): Map<String, Any?> {
      return mapOf(
         "id" to id,
         "title" to title,
         "description" to description,
         "status" to status,
         "projectId" to projectId
      )
   }
}