package educ.unito.myapplication.profile

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.rememberImagePainter
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import educ.unito.myapplication.*
import educ.unito.myapplication.R
import educ.unito.myapplication.databinding.CompleteProfileBinding
import educ.unito.myapplication.profile.ui.theme.ProfileUITheme
import kotlinx.android.synthetic.main.complete_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.io.File
import java.io.InputStream
import java.util.*

class CompleteProfileFragment : Fragment() {

    private var completeProfileView : View? = null

    // riferimento alla Main Activity
    private var myContext: MainActivity? = null

    // The entry point to the Places API
    private lateinit var placesClient: PlacesClient

    companion object {
        fun newInstance() = CompleteProfileFragment()
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private val viewModel by viewModels<CompleteProfileViewModel>()

    // BINDING
    private var _binding: CompleteProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = CompleteProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        view.complete_profile_compose.setContent {
            ProfileUITheme {
                CompleteProfileScreen(requireActivity() as MainActivity, this, viewModel = viewModel)
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        /************** PLACES **************/

        //if (!Places.isInitialized()) {
        Places.initialize(myContext?.applicationContext, resources.getString(R.string.google_maps_key))
        //}
        placesClient = Places.createClient(myContext) as PlacesClient
        /*************************************/

        /************** AUTOCOMPLETE SEARCH BAR Google Map **************/
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_complete_profile) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        autocompleteFragment.setHint("Inserisci la tua città")
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)

        var searchIcon = (autocompleteFragment.view as LinearLayout).getChildAt(0) as ImageView
        searchIcon.setImageDrawable(
            resources.getDrawable(R.drawable.ic_location_black)
        )


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                viewModel.onLocationChanged(place)
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                //Toast.makeText(requireContext().applicationContext, "An error occurred: $status", Toast.LENGTH_SHORT).show()
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })

        // Gestione del Clear Button di Autocomplete
        autocompleteFragment.view?.findViewById<View>(R.id.places_autocomplete_clear_button)
            ?.setOnClickListener { view ->
                autocompleteFragment.setText("")
                view.visibility = View.GONE
            }

        /******************************************************************/

    }


    /**
     * Funzione per avere il riferimento della MainActivity nella variabile myContext
     */
    override fun onAttach(context : Context) {
        super.onAttach(context)
        myContext = context as MainActivity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        viewModel.onLocationChanged(place)
                        Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(ContentValues.TAG, status.statusMessage!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}



@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun CompleteProfileScreen(myContext : MainActivity, fragment : CompleteProfileFragment, viewModel : CompleteProfileViewModel) {

    viewModel.onUsernameChanged(Amplify.Auth.currentUser.username)

    val context = LocalContext.current

    // Serve per levare il focus dai textFields
    val focusManager = LocalFocusManager.current

    // Colore del background del bottone per caricare la foto
    val mainButtonColor = ButtonDefaults.buttonColors(
        backgroundColor = Color.LightGray
    )

    // Colore del background del bottone per confermare
    val uploadButtonColor = ButtonDefaults.buttonColors(
        backgroundColor = Color(red = 204, green = 0, blue = 0)
    )

    // Colonna esterna in cui rientrano tutti gli item di questa pagina
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .verticalScroll(rememberScrollState())
            .absolutePadding(top = 30.dp, bottom = 30.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {

        /************** Bottone Foto **************/

        CaptureImageFromGalleryOrCamera(myContext, mainButtonColor, viewModel)

        /************** TextField USERNAME **************/
        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            enabled = false,
            value = viewModel.username,
            onValueChange = {},
            label = {
                Text(text = "Username")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        /************** TextField DESCRIZIONE **************/
        var isValidDescription = viewModel.description !== ""

        Spacer(modifier = Modifier.size(10.dp))
        OutlinedTextField(
            value = viewModel.description,
            label = {
                if (isValidDescription) Text(text = "Descrizione")
                else Text(text = "Descrizione*")
            },
            colors = if (isValidDescription) {
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(red = 23, green = 114, blue = 69),
                        cursorColor = Color.Black,
                        focusedLabelColor = Color(red = 23, green = 114, blue = 69)
                    )
                }
                else {
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(red = 204, green = 0, blue = 0),
                        cursorColor = Color.Black,
                        focusedLabelColor = Color(red = 204, green = 0, blue = 0)
                    )
                },
            onValueChange = {
                viewModel.onDescriptionChanged(it)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            maxLines = 3,
            isError = !isValidDescription
        )

        /************** Bottone CONFERMA **************/
        Spacer(modifier = Modifier.size(15.dp))
        Button(
            enabled = viewModel.username.isNotBlank() && viewModel.description.isNotBlank() && viewModel.imageUri != null && viewModel.location != null,
            onClick = {

                var imagepath = viewModel.imageUri!!.path //device path
                Log.i("Image path: ", imagepath!!)

                Backend.uploadImageProfile(imagepath, viewModel)

                Thread.sleep(1000)
                Log.i("AWS Storage", "Image key = ${viewModel.imageKey}")


                viewModel.location?.let { place : Place ->
                    val attributes : List<AuthUserAttribute> = listOf(
                        AuthUserAttribute(AuthUserAttributeKey.custom("custom:image"), viewModel.imageKey),
                        AuthUserAttribute(AuthUserAttributeKey.custom("custom:location"), place.name),
                        AuthUserAttribute(AuthUserAttributeKey.custom("custom:description"), viewModel.description),
                    )

                    Amplify.Auth.updateUserAttributes(
                        attributes,
                        { Log.i("AuthDemo", "Updated user attribute = $it") },
                        { Log.e("AuthDemo", "Failed to update user attribute.", it) }
                    )

                    val location = Location(
                        id = place.id!!,
                        lat = place.latLng!!.latitude,
                        lng = place.latLng!!.longitude,
                        name = place.name!!
                    )

                    // store it in the backend
                    Backend.createLocation(location)

                    // add it to LocationData, this will trigger a UI refresh
                    LocationData.addLocation(location)

                    val user = User(
                        id = viewModel.username,
                        username = viewModel.username,
                        location = place.id,
                        description = viewModel.description,
                        image = viewModel.imageKey
                    )

                    // store it in the backend
                    Backend.createUser(user)

                    // add it to UserData, this will trigger a UI refresh
                    UserData.addUser(user)

                    UserData.setCompletedProfile(true)

                }

                Toast.makeText(context, "Profilo completato", Toast.LENGTH_SHORT).show()

            },
            modifier = Modifier.height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = uploadButtonColor,
        )
        {
            Text(
                text = "CONFERMA",
                color = Color.White
            )
        }
    }

}

@Composable
fun CaptureImageFromGalleryOrCamera(myContext: MainActivity, mainButtonColor: ButtonColors, viewModel : CompleteProfileViewModel) {

    val context = LocalContext.current

    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            viewModel.onBitmapChanged(it)
        }

    val launcherGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            val imageuri = Backend.getFilePathFromUri(it, myContext)
            viewModel.onImageUriChanged(imageuri)
        }

    var cameraPermission = false

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        viewModel.imageUri?.let {

            if (Build.VERSION.SDK_INT < 28) {
                viewModel.onBitmapChanged(
                    MediaStore.Images.Media.getBitmap(context.contentResolver,it)
                )
            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                viewModel.onBitmapChanged(ImageDecoder.decodeBitmap(source))
            }

            viewModel.bitmap?.let {  btm ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.absolutePadding(top = 10.dp)
                ) {
                    RoundImage(
                        image = rememberImagePainter(btm),
                        //bitmap = btm.asImageBitmap(),
                        //contentDescription = "user_image",
                        modifier = Modifier
                            .size(200.dp)
                            .weight(3f)
                            .absolutePadding(bottom=20.dp)
                        //modifier = Modifier
                        //    .sizeIn(minWidth = 600.dp, minHeight = 300.dp)
                        //    .padding(0.dp)
                    )
                }
            }
        }

        Button(
            onClick = {

                while (!cameraPermission) {
                    if (ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        val permission = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        )
                        ActivityCompat.requestPermissions(myContext, permission, 121)
                    } else {
                        cameraPermission = true

                        /**
                        val dialog = PickImageDialog.build(PickSetup())
                        .setOnClick(IPickClick() {
                        override fun onGalleryClick() {

                        }
                        override fun onCameraClick() {
                        launcherCamera.launch()
                        }
                        }

                        dialog.show(myContext as FragmentManager)
                         */

                        /**
                        val dialog = PickImageDialog.build(PickSetup())
                        .setOnClick(IPickClick() {
                        override fun onGalleryClick() {

                        }
                        override fun onCameraClick() {
                        launcherCamera.launch()
                        }
                        }

                        dialog.show(myContext as FragmentManager)
                         */

                        /**
                        val dialog = PickImageDialog.build(PickSetup())
                        .setOnClick(IPickClick() {
                        override fun onGalleryClick() {

                        }
                        override fun onCameraClick() {
                        launcherCamera.launch()
                        }
                        }

                        dialog.show(myContext as FragmentManager)
                         */

                        /**
                        val dialog = PickImageDialog.build(PickSetup())
                        .setOnClick(IPickClick() {
                        override fun onGalleryClick() {

                        }
                        override fun onCameraClick() {
                        launcherCamera.launch()
                        }
                        }

                        dialog.show(myContext as FragmentManager)
                         */

                        launcherGallery.launch("image/*")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = mainButtonColor
        ) {
            // Colonna interna al bottone della foto per organizzare l'icona e la scritta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_foreground),
                    contentDescription = "Add",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(50.dp)
                        .absolutePadding(left = 5.dp)
                )
                Text(text = "Carica immagine del profilo")
            }
        }
    }
}

