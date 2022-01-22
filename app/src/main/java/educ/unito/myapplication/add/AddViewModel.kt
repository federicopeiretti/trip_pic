package educ.unito.myapplication.add

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import educ.unito.myapplication.Location

class AddViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var bitmap by mutableStateOf<Bitmap?>(null)
    var imageDevice by mutableStateOf<Uri?>(null) //device
    var imageStorage by mutableStateOf<String>("") //storage
    var location by mutableStateOf<Place?>(null)
    var imageKey by mutableStateOf("")

    fun onDescriptionChanged(newDescription : String) {
        this.description = newDescription
    }

    fun onBitmapChanged(bitmap : Bitmap?) {
        this.bitmap = bitmap
    }

    fun onImageDeviceChanged(uri : Uri?) {
        this.imageDevice = uri
    }

    fun onImageStorageChanged(path : String) {
        this.imageStorage = path
    }

    fun onLocationChanged(location: Place?) {
        this.location = location
    }

    fun onTitleChanged(title : String) {
        this.title = title
    }

    fun onImageKeyChanged(key : String) {
        this.imageKey = key
    }

    fun reset() {
        this.title = ""
        this.description = ""
        this.bitmap = null
        this.imageDevice = null
        this.imageStorage = ""
        this.location = null
        this.imageKey = ""
    }

}