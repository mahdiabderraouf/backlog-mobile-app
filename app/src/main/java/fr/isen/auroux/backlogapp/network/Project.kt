package fr.isen.auroux.backlogapp.network

import android.net.Uri
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Project(
    var title: String? = "",
    var id: String? = "",
    var description: String? = "",
    var imageUrl: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "id" to id,
            "description" to description,
            "imageUrl" to imageUrl
        )
    }
}