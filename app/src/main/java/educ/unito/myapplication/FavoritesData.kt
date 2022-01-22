package educ.unito.myapplication

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

object FavoritesData {

    private const val TAG = "FavoriteData"

    // elenco di tutti gli utenti con gli id dei post salvati come preferiti
    // mario.rossi --> [1, 10, 20, 30]
    // serena.verdi -> [20, 10, 14, 12]
    // ...
    val usersWithFavorites = mutableStateListOf<Favorites>()

    fun addUserWithFavorites(user : Favorites) {
        this.usersWithFavorites.add(user)
        Log.i(TAG, "addUserWithFavorites: {${usersWithFavorites}}")
    }


    fun deleteUserWithFavorites(user : Favorites) {
        this.usersWithFavorites.remove(user)
        Log.i(TAG, "deleteUserWithFavorites")
        Log.i(TAG, "contains: {${usersWithFavorites.contains(user)}") //deve essere false
    }


    fun resetUsersWithFavorites() {
        this.usersWithFavorites.clear()
        Log.i(TAG, "resetUserWithFavorites")
    }

    fun getUserFavorites(user : String) : List<Favorites> {
        val favorites = usersWithFavorites.filter {
            it.user == user
        }
        Log.i(TAG, "Favorites of user $user: $favorites")
        return favorites
    }



    val userFavorites = mutableStateListOf<String>()

    fun addUserFavorite(postId : String) {
        userFavorites.add(postId)
    }

    fun deleteUserFavorite(postId : String) {
        userFavorites.remove(postId)
    }

    fun resetUserFavorites() {
        userFavorites.clear()
    }


}

