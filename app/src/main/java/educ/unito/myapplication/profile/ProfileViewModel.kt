package educ.unito.myapplication.profile

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.core.Amplify
import com.google.android.libraries.places.api.model.Place
import educ.unito.myapplication.Backend
import educ.unito.myapplication.Post
import educ.unito.myapplication.PostData
import educ.unito.myapplication.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // Profilo dell'utente loggato: informazioni relative all'utente loggato

    var loggedUserInfo by mutableStateOf<User?>(null)

    var username by mutableStateOf("mario.rossi")
    var description by mutableStateOf("La vita è come uno specchio: ti sorride se la guardi sorridendo.")
    //var bitmap by mutableStateOf<Bitmap?>(null)
    var location by mutableStateOf<String>("Torino")
    var latitude by mutableStateOf<Double>(45.0735886)
    var longitude by mutableStateOf<Double>(7.605567)
    var imageKey by mutableStateOf("mario.rossi_0")
    var imagePath by mutableStateOf("android.resource://educ.unito.myapplication/drawable/immagineprofilo")

    /**
    val posts = mutableStateListOf<Post>()

    fun addPost(post : Post) {
        this.posts.add(post)
        Log.i("ProfileViewModel", "Posts: {$posts}")
    }

    fun existPost(post : Post) {
        this.posts.contains(
            post
        )
    }
    */

    fun onLoggedUserInfoChanged(user: User?) {
        this.loggedUserInfo = user
    }


    fun onUsernameChanged(user : String) {
        this.username = user
    }

    fun onDescriptionChanged(newDescription : String) {
        this.description = newDescription
    }

    /**
    fun onBitmapChanged(bitmap : Bitmap?) {
        this.bitmap = bitmap
    }
    */

    fun onLocationChanged(location: String) {
        this.location = location
    }

    fun onLongitudeChanged(lng : Double) {
        this.longitude = lng
    }

    fun onLatitudeChanged(lat : Double) {
        this.latitude = lat
    }

    fun onImagePathChanged(path : String) {
        this.imagePath = path
    }

    fun onImageKeyChanged(key : String) {
        this.imageKey = key
    }

    fun reset() {
        this.loggedUserInfo =null
        this.username ="mario.rossi"
        this.description ="La vita è come uno specchio: ti sorride se la guardi sorridendo."
        this.location ="Torino"
        this.latitude = 45.0735886
        this.longitude =7.605567
        this.imageKey ="mario.rossi_0"
        this.imagePath = "android.resource://educ.unito.myapplication/drawable/immagineprofilo"
    }

    /**
    fun resetPosts() {
        this.posts.removeRange(0,posts.size)
    }
    */


}