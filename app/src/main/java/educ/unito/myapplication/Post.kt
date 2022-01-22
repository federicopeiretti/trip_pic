package educ.unito.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.amplifyframework.core.model.temporal.Temporal
import java.util.*


data class Post (
    var id: String,
    var owner: String,
    var ownerName: String,
    var location: String,
    var locationName: String,
    var title: String,
    var description: String,
    var imageKey: String,
    var imageUrl: String,
    var createdAt: Temporal.DateTime? = null
) {

    fun getDrawableFromPath() : Drawable {
        return Drawable.createFromPath(this.imageUrl)!!
    }

    fun getBitmapFromPath() : Bitmap {
        return BitmapFactory.decodeFile(this.imageUrl)
    }

    override fun toString(): String = title

    // return an API NoteData from this Note object
    val data : com.amplifyframework.datastore.generated.model.Post
        get() = com.amplifyframework.datastore.generated.model.Post
            .builder()
            .postOwnerId(this.owner)
            .postLocationId(this.location)
            .description(this.description)
            .title(this.title)
            .id(this.id)
            .imageKey(this.imageKey)
            .imageUrl(this.imageUrl)
            .ownerName(this.ownerName)
            .locationName(this.locationName)
            .build()

    // static function to create a Note from a NoteData API object
    companion object {
        fun from(post : com.amplifyframework.datastore.generated.model.Post) : Post {
            val result = Post(
                post.id,
                post.owner.id,
                post.owner.user,
                post.location.id,
                post.location.name,
                post.title,
                post.description,
                post.imageKey,
                post.imageUrl,
                post.createdAt
            )
            // some additional code will come here later
            return result
        }
    }
}