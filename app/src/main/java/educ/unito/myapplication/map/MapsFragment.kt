package educ.unito.myapplication.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import educ.unito.myapplication.MainActivity
import educ.unito.myapplication.R

import kotlin.collections.ArrayList
import android.widget.RelativeLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import educ.unito.myapplication.Post
import java.io.InputStream


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private var latlngs: ArrayList<LatLng> = ArrayList()
    private val options = MarkerOptions()

    // riferimento alla Main Activity
    private var myContext: MainActivity? = null

    // Google Map
    private var mMap : GoogleMap? = null
    private var mapView : View? = null

    // The entry point to the Places API
    private lateinit var placesClient: PlacesClient

    // Marker temporaneo
    private var tempMarker : Marker? = null


    private var cameraPosition: CameraPosition? = null

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null


    /**
     * Funzione che richiede l'accesso alla posizione GPS corrente
     */
    private fun getLocationPermission() {
        while (!locationPermissionGranted) {
            if (ContextCompat.checkSelfPermission(
                    myContext!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            } else {
                ActivityCompat.requestPermissions(
                    myContext!!, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }
    }


    /**
     * Funzione che verifica i permessi e mostra i risultati
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false

        when(requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap : GoogleMap) {

        mMap = googleMap

        // Accesso alla posizione corrente GPS in Google Map
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap?.uiSettings?.isZoomControlsEnabled = false
        mMap!!.uiSettings.isCompassEnabled = false  //bussola
        mMap?.uiSettings?.isMapToolbarEnabled = false //indicazioni stradali


        moveCurrentLocationButtonOnBottom()

        // Posizionamento in basso del button della posizione attuale in Google Map
        /**
        val locationButton = (mapView!!.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp =  locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)
        */


        /************** MARKER **************/
        mMap?.setOnMarkerClickListener(this)

        var sydney = LatLng(-33.87365, 151.20689)
        var brisbane = LatLng(-27.47093, 153.0235)
        var perth = LatLng(-31.952854, 115.857342)

        latlngs.add(sydney)
        latlngs.add(brisbane)
        latlngs.add(perth)

        for (point in latlngs) {
            options.position(point)
            mMap!!.addMarker(options)?.tag = 0
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
        }
        /*************************************/

    }

    /**
     * Sposta il button relativo alla geolocalizzazione in basso a destra
     */
    private fun moveCurrentLocationButtonOnBottom() {
        val locationButton = (mapView!!.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp =  locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Layout del fragment di Google Map
        mapView = inflater.inflate(R.layout.fragment_maps, container, false)
        return mapView
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myContext!!)

        // Build the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?


        /************** PLACES **************/
        val apiKey : String = R.string.google_maps_key.toString()

        //if (!Places.isInitialized()) {
        Places.initialize(myContext?.applicationContext, resources.getString(R.string.google_maps_key))
        //}
        placesClient = Places.createClient(myContext) as PlacesClient
        /*************************************/


        /************** AUTOCOMPLETE SEARCH BAR Google Map **************/
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.map_autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                tempMarker?.remove()
                tempMarker = mMap!!.addMarker(MarkerOptions().position(place.latLng).title(place.name))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 10f))
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Toast.makeText(myContext?.applicationContext, "An error occurred: $status", Toast.LENGTH_SHORT)
                Log.i(TAG, "An error occurred: $status")
            }

        })

        // Gestione del Clear Button di Autocomplete (rimozione del pin temporaneo)
        autocompleteFragment.view?.findViewById<View>(R.id.places_autocomplete_clear_button)
            ?.setOnClickListener { view ->
                autocompleteFragment.setText("")
                tempMarker?.remove()
                view.visibility = View.GONE
            }


        /******************************************************************/

        mapFragment?.getMapAsync(this)
    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        mMap?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }



    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap?.isMyLocationEnabled = true
                            mMap?.uiSettings?.isMyLocationButtonEnabled = true
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                            mMap?.setOnMyLocationButtonClickListener(this)
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap?.isMyLocationEnabled = false
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        // I've tried just setting the default behavior of the button, but just works after reloading
        // the activity, then I've managed the click event to latest location; now it works for
        // all cases.

        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lastKnownLocation!!.latitude,
                    lastKnownLocation!!.longitude
                ), DEFAULT_ZOOM.toFloat()
            )
        )
        return false
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap?.isMyLocationEnabled = true
                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap?.isMyLocationEnabled = false
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }



    /**
     * Funzione richiamata quando si clicca sul Marker: apre il popup visualizzando foto/video
     */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            //val dialog = MapPinPopup(post = )
            //val fragManager: FragmentManager = myContext!!.supportFragmentManager
            //dialog.show(fragManager, "pinPopup")
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false

    }

    /**
     * Funzione per avere il riferimento della MainActivity nella variabile myContext
     */
    override fun onAttach(context : Context) {
        super.onAttach(context)
        myContext = context as MainActivity
    }

/**
    override fun onResume() {
        super.onResume()
        if (mMap != null) {
            // Accesso alla posizione corrente GPS in Google Map
            getLocationPermission()
            // Turn on the My Location layer and the related control on the map.
            updateLocationUI()
            moveCurrentLocationButtonOnBottom()
        }
    }
    */




    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5

        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

}

























/**

@Composable
private fun MapPinPopup(post : Post) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .padding(4.dp)
    ) {

        //User
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .absolutePadding(top = 0.dp, bottom = 0.dp, right = 0.dp, left = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = post.getOwner(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(2.dp)
                )
            }

            Column() {
                var showMenu by remember { mutableStateOf(false) }
                Icon(
                    // icona cliccabile per gestire la rimozione del post dai preferiti
                    // TODO spostare l'icona sulla destra
                    Icons.Rounded.MoreVert,
                    contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            showMenu = !showMenu
                        }
                )

                DropdownMenu(
                    expanded = showMenu,
                    modifier = Modifier.fillMaxWidth(0.4f),
                    onDismissRequest = { showMenu = false }
                ) {

                    DropdownMenuItem(
                        onClick = {
                            var uri = Uri.parse(post.getImagePath())
                            var stream = context.getContentResolver().openInputStream(uri) as InputStream

                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = "image/*"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                            showMenu = false
                        },
                        //modifier = Modifier.wrapContentSize().sizeIn(minWidth=50.dp),
                    ) {

                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Condividi")
                    }
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                        },
                        //modifier = Modifier.wrapContentSize().sizeIn(minWidth=50.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Modifica")
                    }
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                        },
                        //modifier = Modifier.wrapContentSize().sizeIn(minWidth=50.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Cancella")
                    }
                }

            }
        }

        //Location
        Row(
            modifier = Modifier
                //.fillMaxWidth()
                //.wrapContentHeight()
                .absolutePadding(top = 1.dp, bottom = 1.dp, right = 0.dp, left = 5.dp)
        ) {
            Icon(
                Icons.Rounded.LocationOn,
                contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = post.getLocation(),
                //fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(2.dp)
            )
        }

        //Image
        Row(
            //modifier = Modifier
            //    .fillMaxWidth(3f)
            //.wrapContentSize()
        ) {
            Image(
                painter = post.getImage(),
                contentDescription = null,
                modifier = Modifier
                    .absolutePadding(left = 10.dp, right = 10.dp, top = 1.dp, bottom = 1.dp)
                    .sizeIn(minWidth = 800.dp, minHeight = 300.dp)
            )
        }

        //Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .absolutePadding(top = 5.dp, bottom = 5.dp, right = 0.dp, left = 5.dp)
        ) {
            Text(
                text = post.getTitle(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(2.dp)
            )
        }

        //Description
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .absolutePadding(top = 5.dp, bottom = 5.dp, right = 0.dp, left = 5.dp)
        ) {
            Text(
                text = post.getDescription(),
                //fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}
*/