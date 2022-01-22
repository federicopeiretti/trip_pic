package educ.unito.myapplication.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import com.google.android.libraries.places.api.model.Place

class CompleteProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    var username by mutableStateOf(Amplify.Auth.currentUser.username)
    var description by mutableStateOf("")
    var bitmap by mutableStateOf<Bitmap?>(null)
    var imageUri by mutableStateOf<Uri?>(null) //uri dell'immagine presente sul dispositivo (per gallery)
    var location by mutableStateOf<Place?>(null)
    var imageKey = ""


    fun onUsernameChanged(user : String) {
        this.username = user
    }

    fun onDescriptionChanged(newDescription : String) {
        this.description = newDescription
    }

    fun onBitmapChanged(bitmap : Bitmap?) {
        this.bitmap = bitmap
    }

    fun onImageUriChanged(uri : Uri?) {
        this.imageUri = uri
    }

    fun onLocationChanged(location: Place) {
        this.location = location
    }

    fun reset() {
        this.username = ""
        this.description = ""
        this.bitmap = null
        this.imageUri = null
        this.location = null
        this.imageKey = ""
    }

}