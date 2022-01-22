package educ.unito.myapplication.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amplifyframework.core.Amplify
import educ.unito.myapplication.*
import educ.unito.myapplication.profile.ui.theme.ProfileUITheme

class ProfileFragment : Fragment() {

    // riferimento alla Main Activity
    private var myContext: MainActivity? = null


    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel by viewModels<ProfileViewModel>()

    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
            // update UI
            Log.i("ProfileFragment", "isSignedIn changed : $isSignedUp")

            if (isSignedUp) {
                val currentUser = Amplify.Auth.currentUser
                Log.i("CURRENT LOGGED USER: ", "userID: ${currentUser.userId}")
                Log.i("CURRENT LOGGED USER: ", "username: ${currentUser.username}")

                if (viewModel.username != currentUser.username)
                    Backend.getCurrentUserInfo(currentUser.username, viewModel)

                if (PostData.posts.none()) {
                    Backend.getUserPosts(currentUser.username)
                    //Backend.getUserPosts(currentUser.username, viewModel, activity!!)
                    Log.i("ProfileFragment: ", "getUserPosts")
                }
                Log.i("ProfileFragment: ", "${PostData.posts}")
            }
            else {
                PostData.resetPosts()
                viewModel.onLoggedUserInfoChanged(null)
                viewModel.onUsernameChanged("mario.rossi")
                viewModel.onImageKeyChanged("mario.rossi_0")
                viewModel.onLocationChanged("Torino")
                viewModel.onLatitudeChanged(45.0735886)
                viewModel.onLongitudeChanged(7.605567)
                viewModel.onDescriptionChanged("La vita è come uno specchio: ti sorride se la guardi sorridendo.")
                viewModel.onImagePathChanged("android.resource://educ.unito.myapplication/drawable/immagineprofilo")
            }
        })


        return ComposeView(requireContext()).apply {
            setContent {
                ProfileUITheme {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }


    /**
     * Funzione per avere il riferimento della MainActivity nella variabile myContext
     */
    override fun onAttach(context : Context) {
        super.onAttach(context)
        myContext = context as MainActivity
    }

}