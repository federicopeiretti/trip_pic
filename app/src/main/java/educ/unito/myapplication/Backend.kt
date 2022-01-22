package educ.unito.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoauth.AuthUserSession
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent

import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.auth.AuthUserAttribute
import android.os.Bundle
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import android.provider.MediaStore
import android.provider.MediaStore.Images

import android.graphics.Bitmap
import android.provider.DocumentsContract

import android.os.Build
import android.os.Environment
import android.os.FileUtils
import java.io.*
import android.webkit.MimeTypeMap
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import com.amplifyframework.api.graphql.MutationType
import com.amplifyframework.core.category.Category
import com.amplifyframework.core.category.CategoryConfiguration
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.storage.options.StorageListOptions
import com.amplifyframework.storage.result.StorageListResult
import com.google.android.gms.location.places.Place
import educ.unito.myapplication.add.AddViewModel
import educ.unito.myapplication.profile.CompleteProfileViewModel
import educ.unito.myapplication.profile.ProfileViewModel


object Backend {

    private const val TAG = "Backend"


    fun initialize(applicationContext: Context) : Backend {

        try {

            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())

            Amplify.configure(applicationContext)

            Log.i(TAG, "Initialized Amplify")

        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }

        // REGISTRAZIONE e RECUPERO SESSIONE UTENTE
        Log.i(TAG, "registering hub event")

        Amplify.Hub.subscribe(HubChannel.AUTH) { hubEvent: HubEvent<*> ->

            when (hubEvent.name) {
                InitializationStatus.SUCCEEDED.toString() -> {
                    Log.i(TAG, "Amplify successfully initialized")
                }
                InitializationStatus.FAILED.toString() -> {
                    Log.i(TAG, "Amplify initialization failed")
                }
                else -> {
                    when (AuthChannelEventName.valueOf(hubEvent.name)) {
                        AuthChannelEventName.SIGNED_IN -> {
                            updateUserData(true)
                            Log.i(TAG, "HUB : SIGNED_IN")
                        }
                        AuthChannelEventName.SIGNED_OUT -> {
                            updateUserData(false)
                            Log.i(TAG, "HUB : SIGNED_OUT")
                        }
                        else -> Log.i(TAG, """HUB EVENT:${hubEvent.name}""")
                    }
                }
            }
        }

        Log.i(TAG, "retrieving session status")

        // is user already authenticated (from a previous execution) ?
        Amplify.Auth.fetchAuthSession(
            { result ->
                Log.i(TAG, result.toString())
                val cognitoAuthSession = result as AWSCognitoAuthSession

                // update UI
                this.updateUserData(cognitoAuthSession.isSignedIn)

                when (cognitoAuthSession.identityId.type) {
                    AuthSessionResult.Type.SUCCESS ->
                        Log.i(TAG, "IdentityId: " + cognitoAuthSession.identityId.value)
                    AuthSessionResult.Type.FAILURE ->
                        Log.i(TAG, "IdentityId not present because: " + cognitoAuthSession.identityId.error.toString())
                }
            },
            { error -> Log.i(TAG, error.toString()) }
        )

        return this
    }

    private fun updateUserData(withSignedInStatus : Boolean) {
        UserData.setSignedIn(withSignedInStatus)

        //val posts = PostData.post().value
        //val isEmpty = posts?.isEmpty() ?: false

        // query notes when signed in and we do not have Notes yet
        //if (withSignedInStatus && isEmpty ) {
        //    this.queryPosts()
        //} else {
        //    PostData.resetPost()
        //}
    }

    //sign out
    fun signOut() {
        Log.i(TAG, "Initiate Signout Sequence")

        Amplify.Auth.signOut(
            { Log.i(TAG, "Signed out!") },
            { error -> Log.e(TAG, error.toString()) }
        )
    }

    //sign in
    fun signIn(callingActivity: Activity) {
        Log.i(TAG, "Initiate Signin Sequence")

        Amplify.Auth.signInWithWebUI(
            callingActivity,
            { result: AuthSignInResult ->  Log.i(TAG, result.toString()) },
            { error: AuthException -> Log.e(TAG, error.toString()) }
        )
    }

    // pass the data from web redirect to Amplify libs
    fun handleWebUISignInResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "received requestCode : $requestCode and resultCode : $resultCode")
        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data)
        }
    }


    /*********************** POST ***********************/
    // Estrarre tutti i post degli amici dell'utente corrente loggato
    @Throws(Exception::class)
    fun getAllFriendsPosts(currentUser : String) {

        FriendData.resetAllFriendsPosts()

        val friends = FriendData.userFriends

        Log.i(TAG, "Querying posts")

        Amplify.API.query(
            ModelQuery.list(
                com.amplifyframework.datastore.generated.model.Post::class.java
            ),
            { response ->
                Log.i(TAG, "Queried")
                for (postData in response.data) {
                    if (friends.contains(postData.ownerName)) {
                        FriendData.addAllFriendsPosts(Post.from(postData))
                        Log.i(TAG, "Post dell'amico: $postData")
                    }
                }
            },
            {
                error ->
                    Log.e(TAG, "Query failure", error)
                    throw Exception("Errore: non è stato possibile recuperare i post degli amici")
            }
        )
    }

    //creare un post
    fun createPost(post : Post) {
        Log.i(TAG, "Creating posts")

        Amplify.API.mutate(
            ModelMutation.create(post.data),
            { response ->
                Log.i(TAG, "Created")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Created Post with id: " + response.data.id)

                }
            },
            { error -> Log.e(TAG, "Create failed", error) }
        )
    }

    //cancellare un post
    fun deletePost(post : Post?) {

        if (post == null) return

        Log.i(TAG, "Deleting post $post")

        Amplify.API.mutate(
            ModelMutation.delete(post.data),
            { response ->
                Log.i(TAG, "Deleted")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Deleted Post $response")
                }
            },
            { error -> Log.e(TAG, "Delete failed", error) }
        )
    }


    // posts dell'utente corrente loggato
    fun getUserPosts(user : String) {
        Log.i(TAG, "getUserPosts")

        Amplify.API.query(
            ModelQuery.list(
                com.amplifyframework.datastore.generated.model.Post::class.java,
                com.amplifyframework.datastore.generated.model.Post.POST_OWNER_ID.eq(user)
            ),
            { response ->
                Log.i(TAG, "Queried")
                for (postData in response.data) {
                    Log.i(TAG, "Post: $postData")
                    var post = Post.from(postData)

                    post.imageUrl = "https://myapplication79eda229a8aa41f1a0881204d9c4e19e210000-dev.s3.eu-central-1.amazonaws.com/public/${post.imageKey}"
                    PostData.addPost(post)

                    /**
                    var file = File("${applicationContext.filesDir}/${post.imageKey}.jpg")

                    Amplify.Storage.downloadFile(
                        post.imageKey,
                        file,
                        {
                            Log.i("MyAmplifyApp", "Successfully downloaded: ${it.file.absolutePath}")
                            post.imageUrl = it.file.absolutePath
                        },
                        { Log.e("MyAmplifyApp",  "Download Failure", it) }
                    )
                    */


                    /**
                    Amplify.Storage.getUrl(
                        post.imageKey,
                        { result ->
                            Log.i("GetURLImage", "Successfully generated: ${result.url}")
                            post.imageUrl = result.url.toString()
                            viewModel.addPost(post)
                        },
                        { Log.e("GetURLImage", "URL generation failure", it) }
                    )
                    */

                }
            },
            { error -> Log.e(TAG, "Query failure", error) }
        )
    }




    // post di un amico
    fun getFriendPosts(user : String) {
        FriendData.resetFriendPosts()

        Log.i(TAG, "getFriendPosts")

        Amplify.API.query(
            ModelQuery.list(
                com.amplifyframework.datastore.generated.model.Post::class.java,
                com.amplifyframework.datastore.generated.model.Post.POST_OWNER_ID.eq(user)
            ),
            { response ->
                Log.i(TAG, "Queried")
                for (postData in response.data) {
                    Log.i(TAG, "Post: $postData")
                    var post = Post.from(postData)

                    post.imageUrl = "https://myapplication79eda229a8aa41f1a0881204d9c4e19e210000-dev.s3.eu-central-1.amazonaws.com/public/${post.imageKey}"
                    FriendData.addFriendPost(post)
                }
            },
            {
                error -> Log.e(TAG, "Query failure", error)
            }
        )
    }







    /*********************** USER ***********************/

    //estrarre tutti gli utenti iscritti
    fun queryUsers(currentUser : String) {
        Log.i(TAG, "Querying users")

        Amplify.API.query(
            ModelQuery.list(com.amplifyframework.datastore.generated.model.User::class.java),
            { response ->
                Log.i(TAG, "Queried")
                for (userData in response.data) {
                    Log.i(TAG, "queryUser(): ${userData.user}")
                    // TODO should add all the notes at once instead of one by one (each add triggers a UI refresh)
                    if (userData.user != currentUser) {
                        Log.i(TAG, "queryUser(): IF USER != CURRENT")
                        var utente = User.from(userData)
                        utente.imagePath = "https://myapplication79eda229a8aa41f1a0881204d9c4e19e210000-dev.s3.eu-central-1.amazonaws.com/public/" + utente.image
                        UserData.addUser(utente)
                    }
                }
            },
            { error -> Log.e(TAG, "Query failure", error) }
        )
    }

    //creazione di un utente
    fun createUser(user : User) {
        Log.i(TAG, "Creating users")

        Amplify.API.mutate(
            ModelMutation.create(user.data),
            { response ->
                Log.i(TAG, "Created")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Created User with id: " + response.data.id)
                }
            },
            { error -> Log.e(TAG, "Create failed", error) }
        )
    }


    //cancellazione di un utente
    fun deleteUser(user : User?) {

        if (user == null) return

        Log.i(TAG, "Deleting user $user")

        Amplify.API.mutate(
            ModelMutation.delete(user.data),
            { response ->
                Log.i(TAG, "Deleted")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Deleted User $response")
                }
            },
            { error -> Log.e(TAG, "Delete failed", error) }
        )
    }


    //verificare se il profilo è completato oppure no
    fun isCompletedProfile(username : String) {

        if (username == null) return

        Log.i(TAG, "isCompletedProfile $username")

        Amplify.API.query(

            ModelQuery.list(com.amplifyframework.datastore.generated.model.User::class.java,
                com.amplifyframework.datastore.generated.model.User.ID.eq(username)),

            { response ->

                Log.i("isCompletedProfile", "response.hasData(): ${response.hasData()}")
                Log.i("isCompletedProfile", "response.hasErrors(): ${response.hasErrors()}")
                Log.i("isCompletedProfile", "response.data: ${response.data}")
                Log.i("isCompletedProfile", "response.data.items.none(): ${response.data.items.none()}")

                if (response.data.items.none()) {
                    UserData.setCompletedProfile(false)
                    Log.i("isCompletedProfile", "isCompletedProfile: FALSE")
                }
                else {
                    UserData.setCompletedProfile(true)
                    Log.i("isCompletedProfile", "isCompletedProfile: TRUE")
                }

                /**
                if (result.hasData()) {
                    UserData.setCompletedProfile(true)
                    Log.i("isCompletedProfile", "isCompletedProfile: TRUE")
                }
                else {
                    UserData.setCompletedProfile(false)
                    Log.i("isCompletedProfile", "isCompletedProfile: FALSE")
                }
                */
            },
            { Log.e("isCompletedProfile", "Query failed", it) }
        )
    }




    /*********************** LOCATION ***********************/

    //estrae tutte le Locations salvate in db
    fun getLocations() {
        Log.i(TAG, "Querying locations")

        Amplify.API.query(
            ModelQuery.list(com.amplifyframework.datastore.generated.model.Location::class.java),
            { response ->
                Log.i(TAG, "Queried")
                for (locationData in response.data) {
                    Log.i(TAG, locationData.name)
                    // TODO should add all the notes at once instead of one by one (each add triggers a UI refresh)
                    LocationData.addLocation(Location.from(locationData))
                    Log.i(TAG, "Backend.getLocations: ${LocationData.locations}")
                }
            },
            { error -> Log.e(TAG, "Query failure", error) }
        )
    }

    //crea Location
    fun createLocation(location : Location) {
        Log.i(TAG, "Creating location")

        Amplify.API.mutate(
            ModelMutation.create(location.data),
            { response ->
                Log.i(TAG, "Created")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Created Location with id: " + response.data.id)
                }
            },
            { error -> Log.e(TAG, "Create failed", error) }
        )
    }





    /*********************** FRIENDS ***********************/

    //creazione della tupla relativa all'utente loggato nella tabella Friend
    fun createUserFriends(friends : Friend) {
        Log.i(TAG, "Creating user friends...")

        Amplify.API.mutate(
            ModelMutation.create(friends.data),
            { response ->
                Log.i(TAG, "Created")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Created user friends with id: " + response.data.id)
                }
            },
            { error -> Log.e(TAG, "Create failed", error) }
        )
    }


    // Aggiunta di un amico
    @Throws(Exception::class)
    fun addUserFriend(user : String, friend : String) {

        Amplify.API.query(
            ModelQuery.list(
                com.amplifyframework.datastore.generated.model.Friend::class.java,
                com.amplifyframework.datastore.generated.model.Friend.FRIEND_USER_ID.eq(user)
            ),
            {

                var result = it.data.items
                Log.i("Backend - addUserFriend()", "Response = ${result}")

                //se NON esiste, creo la tupla in db
                if (result.none()) {
                    val friendship = Friend(
                        id = user,
                        user = user,
                        friends = listOf(friend)
                    )
                    createUserFriends(friendship)
                    FriendData.addUserWithFriends(friendship)
                    FriendData.addUserFriends(friend)
                    getAllFriendsPosts(user)
                }

                //se esiste, controllo se è già presente nella lista degli amici
                else {

                    var amico = result.first()

                    //amico già presente
                    if (amico.friends.contains(friend)) {
                        throw IOException("$friend è già presente nella lista dei tuoi amici!")
                    }

                    //si aggiunge l'amico (update)
                    else {
                        if (amico.friends.add(friend)) {
                            Log.i("Backend - addUserFriend()", "Updated item = ${amico}")

                            Amplify.API.mutate(
                                ModelMutation.update(amico),
                                { response ->
                                    Log.i(
                                        "Backend - addUserFriend()",
                                        "Updated user in DB: " + amico
                                    )
                                    FriendData.addUserFriends(friend)
                                    getAllFriendsPosts(user)
                                },
                                { error ->
                                    Log.e("Backend - addUserFriend()", "Update failed", error)
                                    throw Exception("Errore: l'utente non è stato aggiunto come amico.")
                                }
                            )
                        } else {
                            Log.e("Backend - addUserFriend()", "Query failed")
                            throw Exception("Errore: l'utente non è stato aggiunto come amico.")
                        }
                    }
                }
            },
            {
                Log.e("Backend - addUserFriend()", "Query failed", it)
                throw Exception("Errore: l'utente non è stato aggiunto come amico.")
            }
        )
    }


    //estrae gli amici dell'utente corrente loggato
    @Throws(Exception::class)
    fun getUserFriends(currentUser: String) {

        Amplify.API.query(
            ModelQuery.list(
                com.amplifyframework.datastore.generated.model.Friend::class.java,
                com.amplifyframework.datastore.generated.model.Friend.FRIEND_USER_ID.eq(currentUser)
            ),
            {
                Log.i("Backend - getUserFriends()", "Response = ${it.data}")

                if (!it.data.items.none()) {
                    var result = it.data.items.first()
                    Log.i("Backend - getUserFriends()", "Response = ${result}")

                    for (item in result.friends) {
                        FriendData.addUserFriends(item)
                    }
                    Log.i(
                        "Backend - getUserFriends()",
                        "FriendData.userFriends = ${FriendData.userFriends}"
                    )
                }
            },
            {
                Log.e("Backend - getUserFriends()", "Errore: non è stato possibile recuperare la lista dei tuoi amici")
                throw Exception("Errore: non è stato possibile recuperare la lista dei tuoi amici")
            }
        )

    }




    /*********************** FAVORITES ***********************/

    //creazione della tupla relativa all'utente loggato nella tabella Favorites
    fun createUserFavorites(favorites : Favorites) {
        Log.i(TAG, "Creating user favorites...")

        Amplify.API.mutate(
            ModelMutation.create(favorites.data),
            { response ->
                Log.i(TAG, "Created")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Created user favorites with id: " + response.data.id)
                }
            },
            { error -> Log.e(TAG, "Create failed", error) }
        )
    }



    // Aggiungere/Eliminare un post nei/dai preferiti
    @Throws(Exception::class)
    fun addUserFavorite(user : String, post : String) {

        Amplify.API.query(
            ModelQuery.get(
                com.amplifyframework.datastore.generated.model.Favorites::class.java,
                user
                //com.amplifyframework.datastore.generated.model.Favorites.ID.eq(user)
            ),
            {
                Log.i("Backend - addUserFavorite()", "UTENTE CONNESSO = ${user}")
                Log.i("Backend - addUserFavorite()", "Response = ${it}")

                //se NON esiste, creo la tupla in db
                if (it.data == null || it.data.post.none()) {
                    val favorite = Favorites(
                        id = user,
                        user = user,
                        favorites = listOf(post)
                    )
                    createUserFavorites(favorite)
                    FavoritesData.addUserWithFavorites(favorite)
                    FavoritesData.addUserFavorite(post)
                }

                //se esiste, controllo se è già presente nella lista dei preferiti
                else {

                    var preferito = it.data

                    //post già presente nei favoriti
                    if (preferito.post.contains(post)) {

                        //eliminare il post dai preferiti
                        if(preferito.post.remove(post)) {

                            val updatedFavorites = Favorites(
                                id = preferito.id,
                                user = preferito.favoritesUserId,
                                favorites = preferito.post
                            )

                            Log.i(
                                "Backend - addUserFavorite(): cancellazione del post dai preferiti",
                                "Creazione di updatedFavorites: ${updatedFavorites.data}"
                            )

                            Amplify.API.mutate(
                                ModelMutation.update(
                                    updatedFavorites.data
                                ),
                                { response ->
                                    FavoritesData.deleteUserFavorite(post)
                                    Log.i(
                                        "Backend - addUserFavorite(): cancellazione del post dai preferiti",
                                        "Il post è stato cancellato dai preferiti: " + response
                                    )
                                },
                                { error ->
                                    Log.e(
                                        "Backend - addUserFavorite(): cancellazione del post dai preferiti",
                                        "Update failed",
                                        error
                                    )
                                    throw Exception("Errore: il post non è stato eliminato.")
                                }
                            )

                        }
                    }

                    //si aggiunge il post (update)
                    else {
                        if (preferito.post.add(post)) {
                            Log.i("Backend - addUserFavorite()", "Updated item = ${preferito}")

                            val updatedFavorites = Favorites(
                                id = preferito.id,
                                user = preferito.favoritesUserId,
                                favorites = preferito.post
                            )

                            Log.i(
                                "Backend - addUserFavorite(): cancellazione del post dai preferiti",
                                "Creazione di updatedFavorites: ${updatedFavorites.data}"
                            )

                            Amplify.API.mutate(
                                ModelMutation.update(updatedFavorites.data),
                                { response ->
                                    Log.i(
                                        "Backend - addUserFavorite()",
                                        "Updated user in DB: $response"
                                    )
                                    FavoritesData.addUserFavorite(post)
                                },
                                { error ->
                                    Log.e("Backend - addUserFavorite()", "Update failed", error)
                                    throw Exception("Errore: il post non è stato aggiunto come preferito.")
                                }
                            )
                        } else {
                            Log.e("Backend - addUserFavorite()", "Query failed")
                            throw Exception("Errore: il post non è stato aggiunto come preferito.")
                        }
                    }
                }
            },
            {
                Log.e("Backend - addUserFavorite()", "Query failed", it)
                throw Exception("Errore: il post non è stato aggiunto come preferito.")
            }
        )

    }





    fun getUserFavorites(currentUser : String) {
        Log.i(TAG, "Querying favorites")

        Amplify.API.query(
            ModelQuery.get(
                com.amplifyframework.datastore.generated.model.Favorites::class.java,
                currentUser
            ),
            { response ->
                if (response.data != null) {
                    Log.i(TAG, "Queried: $response")
                    for (favoritesData in response.data.post) {
                        Log.i(TAG, "$favoritesData")
                        // TODO should add all the notes at once instead of one by one (each add triggers a UI refresh)
                        FavoritesData.addUserFavorite(favoritesData)
                    }
                    Log.i(TAG, "Backend.getLocations: ${FavoritesData.userFavorites}")
                }
            },
            { error -> Log.e(TAG, "Query failure", error) }
        )
    }









    /*********************** IMAGES ***********************/

    //caricamento immagine profilo
    fun uploadImageProfile(path : String, viewModel : CompleteProfileViewModel) {
        var username = Amplify.Auth.currentUser.username

        viewModel.imageKey = username + "_0"
        Log.i("MyAmplifyApp", "uploadFile - value of key: ${viewModel.imageKey}")

        val exampleFile = File(path)
        Amplify.Storage.uploadFile(viewModel.imageKey, exampleFile,
            { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
            { Log.e("MyAmplifyApp", "Upload failed", it) }
        )
    }


    //get URL dell'immagine profilo
    fun getURLImageProfile(imagekey : String, viewModel : ProfileViewModel) {
        Amplify.Storage.getUrl(
            imagekey,
            {
                Log.i("GetURLImage", "Successfully generated: ${it.url.path}")
                viewModel.onImagePathChanged(it.url.path)
                //UserData.setImageProfile(it.url.path)
            },
            { Log.e("GetURLImage", "URL generation failure", it) }
        )
    }


    //caricamento immagine post
    fun uploadFile(path : String, viewModel : AddViewModel) {
        var username = Amplify.Auth.currentUser.username

        Amplify.Storage.list("",
            { result ->
                var items = result.items.filter {
                    it.key.startsWith(username+"_")
                }
                var lastItem = items.lastOrNull()
                var lastID = 0
                if (lastItem != null) {
                    lastID = lastItem.key.removePrefix(username+"_").toInt()
                }
                var currentID = lastID + 1
                viewModel.onImageKeyChanged(username + "_" + currentID)

                Log.i("MyAmplifyApp", "uploadFile - value of key: ${viewModel.imageKey}")

                val exampleFile = File(path)
                Amplify.Storage.uploadFile(viewModel.imageKey, exampleFile,
                    { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
                    { Log.e("MyAmplifyApp", "Upload failed", it) }
                )

                Amplify.Storage.getUrl(
                    viewModel.imageKey,
                    { result ->
                        Log.i("GetURLImage", "Successfully generated: ${result.url}")
                        viewModel.onImageStorageChanged(result.url.toString())
                    },
                    { Log.e("GetURLImage", "URL generation failure", it) }
                )
            },
            { Log.e("MyAmplifyApp", "List failure", it) }
        )
    }



    /*********************** INFO CURRENT LOGGED USER ***********************/

    fun getCurrentUserInfo(loggedUser : String, viewModel : ProfileViewModel) {
        Amplify.API.query(
            ModelQuery.get(com.amplifyframework.datastore.generated.model.User::class.java, loggedUser),
            { it ->
                Log.i("MyAmplifyApp", "Query results = ${(it.data)}")

                // loggedUserInfo (mutableStateOf<User>)
                var user = User.from(it.data)
                viewModel.onLoggedUserInfoChanged(user)

                // string
                viewModel.onUsernameChanged(it.data.user)
                viewModel.onImageKeyChanged(it.data.image)
                viewModel.onLocationChanged(it.data.location.name)
                viewModel.onLatitudeChanged(it.data.location.lat)
                viewModel.onLongitudeChanged(it.data.location.lng)
                viewModel.onDescriptionChanged(it.data.description)

                Amplify.Storage.getUrl(
                    user.image,
                    { result ->
                        Log.i("GetURLImage", "Successfully generated: ${result.url}")
                        viewModel.onImagePathChanged(result.url.toString())
                    },
                    { Log.e("GetURLImage", "URL generation failure", it) }
                )
            },
            {
                Log.e("MyAmplifyApp", "Query failed", it)
            }
        )
    }



    /*********************** FILES ***********************/

    /**
     * Convertire da Uri content:// (ActivityResultContracts.GetContent)
     * al file path assoluto del file che risiede nella memoria del telefono
     */
    @Throws(IOException::class)
    open fun getFilePathFromUri(uri: Uri?, myContext : Context): Uri? {
        val fileName = getFileName(uri, myContext)
        val file = File(myContext.externalCacheDir, fileName)
        file.createNewFile()
        FileOutputStream(file).use { outputStream ->
            if (uri != null) {
                myContext.contentResolver.openInputStream(uri).use { inputStream ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(inputStream!!, outputStream)
                    } //Simply reads input to output stream
                    outputStream.flush()
                }
            }
        }
        return Uri.fromFile(file)
    }

    fun getFileName(uri: Uri?, myContext: Context): String? {
        var fileName = getFileNameFromCursor(uri, myContext)
        if (fileName == null) {
            val fileExtension = getFileExtension(uri, myContext)
            fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""
        } else if (!fileName.contains(".")) {
            val fileExtension = getFileExtension(uri, myContext)
            fileName = "$fileName.$fileExtension"
        }
        return fileName
    }

    fun getFileExtension(uri: Uri?, myContext: Context): String? {
        val fileType = uri?.let { myContext.contentResolver.getType(it) }
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    fun getFileNameFromCursor(uri: Uri?, myContext: Context): String? {
        val fileCursor: Cursor? = uri?.let {
            myContext.contentResolver
                .query(it, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        }
        var fileName: String? = null
        if (fileCursor != null && fileCursor.moveToFirst()) {
            val cIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cIndex != -1) {
                fileName = fileCursor.getString(cIndex)
            }
        }
        return fileName
    }



    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }









}