package educ.unito.myapplication.map

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import educ.unito.myapplication.Post
import educ.unito.myapplication.PostMarkerItem

class MapViewModel : ViewModel() {

    val posts = mutableStateListOf<PostMarkerItem>()

    fun addPost(post : PostMarkerItem) {
        this.posts.add(post)
        Log.i("MapViewModel", "Posts: {$posts}")
    }

}