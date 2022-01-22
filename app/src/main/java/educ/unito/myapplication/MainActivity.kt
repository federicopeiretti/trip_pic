package educ.unito.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.services.cognitoidentityprovider.model.UsernameAttributeType
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.core.Amplify
import com.google.android.material.floatingactionbutton.FloatingActionButton
import educ.unito.myapplication.add.AddFragment
import educ.unito.myapplication.favorites.FavoritesFragment
import educ.unito.myapplication.map.MapFragmentCompose
import educ.unito.myapplication.map.MapsFragment
import educ.unito.myapplication.profile.CompleteProfileFragment
import educ.unito.myapplication.profile.ProfileFragment
import educ.unito.myapplication.profile.ProfileViewModel
import educ.unito.myapplication.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map_compose.*
import java.lang.Compiler.enable



class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        var currentUser = "mario.rossi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()

        //val homeFragment = MapsFragment()
        val homeFragment = MapFragmentCompose()
        val searchFragment = SearchFragment()
        val addFragment = AddFragment()
        val favoritesFragment = FavoritesFragment()
        val profileFragment = ProfileFragment()
        val completeProfileFragment = CompleteProfileFragment()


        //Authentication
        setupAuthButton(UserData)

        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
            // update UI
            Log.i(TAG, "isSignedIn changed : $isSignedUp")

            if (isSignedUp) {

                currentUser = Amplify.Auth.currentUser.username


                Thread.sleep(1000)

                fabAuth.setImageResource(R.drawable.ic_baseline_lock_open)
                //Log.i("CURRENT LOGGED USER: ", "userID: ${currentUser.userId}")
                Log.i("CURRENT LOGGED USER: ", "username: ${currentUser}")


                // VERIFICA SE PROFILO COMPLETATO
                Backend.isCompletedProfile(currentUser)


                // LOCATIONS
                Log.i(TAG, "LocationData.location(): ${LocationData.locations}")

                if (LocationData.locations.isEmpty()) {
                    Backend.getLocations()
                    Log.i(TAG, "LocationData.location(): ${LocationData.locations}")
                }
                else {
                    Log.i(TAG, "LocationData.location(): ${LocationData.locations}")
                    var torino = LocationData.getLocation("Torino")
                    Log.i(TAG, "LocationData.getLocation(Torino): ${torino.data}")
                }



                // PROFILO COMPLETATO
                UserData.isCompletedProfile.observe(this, Observer<Boolean> { isCompletedProfile ->
                    Log.i(TAG, "isCompletedProfile changed: $isCompletedProfile")

                    if (isCompletedProfile) {
                        setCurrentFragment(homeFragment)
                        enableBottomNavigationBar(true)
                    }
                    else {
                        setCurrentFragment(completeProfileFragment)
                        enableBottomNavigationBar(false)
                    }
                })


                // ELENCO DEI POST DELL'UTENTE LOGGATO
                if (PostData.posts.none()) {
                    Backend.getUserPosts(currentUser)
                    Log.i("MainActivity: ", "getUserPosts")
                }
                Log.i("MainActivity: ", "${PostData.posts}")


                // ELENCO DEGLI UTENTI ISCRITTI NELLA PIATTAFORMA
                if (UserData.users.none()) {
                    Backend.queryUsers(currentUser)
                    Log.i("MainActivity: ", "queryUsers() - UserData.users: ${UserData.users}")
                }
                else {
                    Log.i("MainActivity: ", "UserData.users: ${UserData.users}")
                }

                // ELENCO AMICI DELL'UTENTE LOGGATO
                if (FriendData.userFriends.none()) {
                    try {
                        Backend.getUserFriends(currentUser)
                    }
                    catch(e : Exception) {
                        Toast
                            .makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
                else {
                    Log.i("MainActivity: ", "FriendData.userFriends: ${FriendData.userFriends}")
                }


                // ELENCO DEI POST DEGLI AMICI
                if (FriendData.allFriendsPosts.none()) {
                    try {
                        Backend.getAllFriendsPosts(currentUser)
                    }
                    catch(e : Exception) {
                        Toast
                            .makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }


                // PREFERITI DELL'UTENTE LOGGATO
                if (FavoritesData.userFavorites.none()) {
                    try {
                        Backend.getUserFavorites(currentUser)
                    }
                    catch(e : Exception) {
                        Toast
                            .makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }


            } else {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock)
                setCurrentFragment(homeFragment)
                enableBottomNavigationBar(true)
                currentUser = "mario.rossi"
                PostData.resetPosts()
                PostData.resetPostMarkerList()
                UserData.resetUsers()
                FriendData.resetUserFriends()
                FriendData.resetUsersWithFriends()
                FriendData.resetAllFriendsPosts()
                //reset di tutto --> cancellare i dati dai viewModel e dai PostData/UserData...
            }
        })


        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home->setCurrentFragment(homeFragment)
                R.id.search->setCurrentFragment(searchFragment)
                R.id.add->setCurrentFragment(addFragment)
                R.id.favorites->setCurrentFragment(favoritesFragment)
                R.id.profile->setCurrentFragment(profileFragment)
            }
            true
        }
    }

    fun setCurrentFragment(fragment:Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    // anywhere in the MainActivity class
    private fun setupAuthButton(userData: UserData) {
        // register a click listener
        fabAuth.setOnClickListener { view ->
            val authButton = view as FloatingActionButton
            if (userData.isSignedIn.value!!) {
                Backend.signOut()
                authButton.setImageResource(R.drawable.ic_baseline_lock)
            } else {
                Log.i(TAG, "get supported browser: " + getSupportedBrowserPackage().toString())
                Backend.signIn(this)
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
            }
        }
    }

    // receive the web redirect after authentication
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Backend.handleWebUISignInResponse(requestCode, resultCode, data)
    }

    private fun getSupportedBrowserPackage(): Collection<String>? {
        val packageManager: PackageManager = this.packageManager
        // Get default VIEW intent handler.
        val activityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = packageManager.queryIntentActivities(activityIntent, 0)
        val packageNamesSupportingCustomTabs: MutableList<String> = ArrayList()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
                .setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
                .setPackage(info.activityInfo.packageName)
            // Check if this package also resolves the Custom Tabs service.
            if (packageManager.resolveService(serviceIntent, 0) != null) {
                packageNamesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }
        return packageNamesSupportingCustomTabs
    }

    fun enableBottomNavigationBar(enable: Boolean) {
        for (i in 0 until bottomNavigationView.getMenu().size()) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(enable)
        }
    }

}