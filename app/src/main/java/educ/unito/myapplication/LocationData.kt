package educ.unito.myapplication

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object LocationData {

    private const val TAG = "LocationData"

    val locations = mutableStateListOf<Location>()

    fun addLocation(location : Location) {
        this.locations.add(location)
        Log.i("LocationData", "Locations: {${locations}}")
    }

    fun deleteLocation(location : Location) {
        this.locations.remove(location)
        Log.i("LocationData", "deleteLocation")
        Log.i("LocationData", "contains: {${this.locations.contains(location)}")
    }

    fun resetLocations() {
        this.locations.clear()
    }

    fun getLocation(name : String) : Location {
        val location = locations.filter {
            it.name == name
        }.first()
        Log.i(this.TAG, "getLocation : $location")
        return location
    }


    /**
    private val _location = MutableLiveData<MutableList<Location>>(mutableListOf())

    // please check https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun notifyObserver() {
        this._location.notifyObserver()
    }

    fun location() : LiveData<MutableList<Location>> = this._location

    fun addLocation(l : Location) {
        val location = this._location.value
        if (location != null) {
            location.add(l)
            this._location.notifyObserver()
        } else {
            Log.e(this.TAG, "addLocation : location collection is null !!")
        }
    }
    */



}