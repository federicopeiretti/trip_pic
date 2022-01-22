package educ.unito.myapplication

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.core.model.temporal.Temporal


object PostData {
    private const val TAG = "PostData"

    val posts = mutableStateListOf<Post>()

    fun addPost(post : Post) {
        this.posts.add(post)
        Log.i("PostData", "Posts: {$posts}")
    }

    fun deletePost(post : Post) {
        this.posts.remove(post)
        Log.i("PostData", "deletePost")
        Log.i("PostData", "contains: {${this.posts.contains(post)}")
    }

    fun resetPosts() {
        this.posts.clear()
    }



    val postMarkerList = mutableStateListOf<PostMarkerItem>()

    fun addPostMarker(post : PostMarkerItem) {
        this.postMarkerList.add(post)
        Log.i("PostData", "Post Marker List: {$postMarkerList}")
    }

    fun deletePostMarker(post : PostMarkerItem) {
        this.postMarkerList.remove(post)
        Log.i("PostData", "deletePostMarker")
        Log.i("PostData", "contains: {${this.postMarkerList.contains(post)}")
    }

    fun resetPostMarkerList() {
        this.postMarkerList.clear()
    }

    fun createPostMarker(post : Post) {
        var location = LocationData.getLocation(post.locationName)
        var item = PostMarkerItem(
            lat = location.lat,
            lng = location.lng,
            title = post.locationName,
            imagePath = post.imageUrl,
            user = post.ownerName
        )
        Log.i("PostData", "Post Marker Item IMAGE: {${post.imageUrl}")
        addPostMarker(item)
    }

    fun createPostMarkerList() {
        for (post in posts) {
            createPostMarker(post)
        }
        if (!FriendData.allFriendsPosts.isEmpty()) {
            for (friendPost in FriendData.allFriendsPosts) {
                createPostMarker(friendPost)
            }
        }
    }



}