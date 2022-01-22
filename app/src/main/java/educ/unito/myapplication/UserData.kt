package educ.unito.myapplication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object UserData {

    private const val TAG = "UserData"

    // signed in status
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    fun setSignedIn(newValue : Boolean) {
        // use postvalue() to make the assignation on the main (UI) thread
        _isSignedIn.postValue(newValue)
    }



    // status profilo completato
    private val _isCompletedProfile = MutableLiveData<Boolean>(false)
    var isCompletedProfile: LiveData<Boolean> = _isCompletedProfile

    fun setCompletedProfile(newValue : Boolean) {
        _isCompletedProfile.postValue(newValue)
    }




    // Elenco degli utenti
    val users = mutableStateListOf<User>()

    fun addUser(user : User) {
        this.users.add(user)
        Log.i("UserData", "Users: {$users}")
    }

    fun deleteUser(user : User) {
        this.users.remove(user)
        Log.i("UserData", "deleteUser")
        Log.i("UserData", "contains: {${this.users.contains(user)}")
    }

    fun resetUsers() {
        this.users.clear()
    }


}