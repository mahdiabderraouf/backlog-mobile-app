package fr.isen.mahdi.socialnetwork.network

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

@IgnoreExtraProperties
class Like(var userId: String? = "", var postId: String? = "") {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "postId" to postId
        )
    }
}