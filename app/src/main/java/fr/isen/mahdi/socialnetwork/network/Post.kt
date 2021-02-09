package fr.isen.mahdi.socialnetwork.network

import android.net.Uri
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Post(
    var userId: String? = "",
    var id: String? = "",
    var title: String? = "",
    var body: String? = "",
    var imageUrl: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "id" to id,
            "title" to title,
            "body" to body,
            "imageUrl" to imageUrl
        )
    }
}