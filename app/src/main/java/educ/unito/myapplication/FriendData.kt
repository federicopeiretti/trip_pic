package educ.unito.myapplication

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

object FriendData {

    private const val TAG = "FriendData"

    // elenco di tutti gli utenti e i loro amici
    // mario.rossi --> [serena.verdi, giovanni.bianchi...]
    // serena.verdi -> [mario.rossi, giovanni.bianchi]
    // ...
    val usersWithFriends = mutableStateListOf<Friend>()

    fun addUserWithFriends(user : Friend) {
        this.usersWithFriends.add(user)
        Log.i(TAG, "addUserWithFriends: {${usersWithFriends}}")
    }


    fun deleteUserWithFriends(user : Friend) {
        this.usersWithFriends.remove(user)
        Log.i(TAG, "deleteUserWithFriends")
        Log.i(TAG, "contains: {${this.usersWithFriends.contains(user)}") //deve essere false
    }


    fun resetUsersWithFriends() {
        this.usersWithFriends.clear()
    }


    fun getUserFriends(user : String) : List<Friend> {
        val friends = usersWithFriends.filter {
            it.user == user
        }
        Log.i(TAG, "Friends of user $user: $friends")
        return friends
    }


    //amici dell'utente corrente loggato
    val userFriends = mutableStateListOf<String>()

    fun addUserFriends(friend : String) {
        userFriends.add(friend)
    }

    fun deleteUserFriends(friend : String) {
        userFriends.remove(friend)
    }

    fun resetUserFriends() {
        userFriends.clear()
    }



    // post dell'amico selezionato
    val friendPosts = mutableStateListOf<Post>()

    fun addFriendPost(post : Post) {
        this.friendPosts.add(post)
        Log.i(TAG, "Posts: {$friendPosts}")
    }

    fun deleteFriendPost(post : Post) {
        this.friendPosts.remove(post)
        Log.i(TAG, "deletePost")
        Log.i(TAG, "contains: {${this.friendPosts.contains(post)}")
    }

    fun resetFriendPosts() {
        this.friendPosts.clear()
    }




    val allFriendsPosts = mutableStateListOf<Post>()

    fun addAllFriendsPosts(post : Post) {
        this.allFriendsPosts.add(post)
        Log.i(TAG, "addAllFriendsPosts() - Posts: {$friendPosts}")
    }

    fun deleteAllFriendsPosts(post : Post) {
        this.allFriendsPosts.remove(post)
        Log.i(TAG, "deleteAllFriendsPosts() - deletePost")
        Log.i(TAG, "deleteAllFriendsPosts() - contains: {${this.friendPosts.contains(post)}")
    }

    fun resetAllFriendsPosts() {
        this.allFriendsPosts.clear()
        Log.i(TAG, "resetAllFriendsPosts()")
    }



}