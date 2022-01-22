package educ.unito.myapplication.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.ktx.awaitMap
import educ.unito.myapplication.MainActivity
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import com.amplifyframework.core.Amplify
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import educ.unito.myapplication.PostMarkerItem
import kotlin.random.Random

import com.google.maps.android.clustering.view.DefaultClusterRenderer
import educ.unito.myapplication.PostData
import educ.unito.myapplication.R
import educ.unito.myapplication.databinding.FragmentMapComposeBinding
import kotlinx.android.synthetic.main.fragment_map_compose.view.*


class MapFragmentCompose : Fragment(),
        GoogleMap.OnMyLocationButtonClickListener
{

    // riferimento alla Main Activity
    //private var myContext: MainActivity? = null

    var mClusterManager : ClusterManager<PostMarkerItem>? = null

    //var latlngs: ArrayList<LatLng> = ArrayList()
    //val options = MarkerOptions()


    private lateinit var mMap : GoogleMap
    private lateinit var mapView : MapView


    private var locationPermissionGranted = false

    // default location se permesso negato : Torino
    private val defaultLocation = LatLng(45.0702388, 7.6000496)

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null


    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // The entry point to the Places API
    private lateinit var placesClient: PlacesClient

    private var cameraPosition: CameraPosition? = null

    // Marker temporaneo
    private var tempMarker : Marker? = null

    // BINDING
    private var _binding: FragmentMapComposeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    companion object {
        fun newInstance() = MapFragmentCompose()

        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5

        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private lateinit var viewModel: MapViewModel

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapComposeBinding.inflate(inflater, container, false)
        val view = binding.root

        view.map_compose.setContent {
            val mapView = rememberMapViewWithLifecycle()
            MapViewContainer(mapView, this@MapFragmentCompose)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        /************** PLACES **************/
        val apiKey : String = R.string.google_maps_key.toString()

        //if (!Places.isInitialized()) {
        Places.initialize(requireContext().applicationContext, resources.getString(R.string.google_maps_key))
        //}
        placesClient = Places.createClient(requireContext()) as PlacesClient
        /*************************************/

        /************** AUTOCOMPLETE SEARCH BAR Google Map **************/
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.map_autocomplete_fragment_compose) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                tempMarker?.remove()
                tempMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(place.latLng)
                        .title(place.name)
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 10f))
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Toast.makeText(requireContext().applicationContext, "An error occurred: $status", Toast.LENGTH_SHORT).show()
                Log.i(ContentValues.TAG, "An error occurred: $status")
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

    }


    fun getGoogleMap() : GoogleMap {
        return this.mMap
    }

    fun setGoogleMap(googleMap : GoogleMap) {
        this.mMap = googleMap
    }

    fun getMapView() : MapView {
        return this.mapView
    }

    fun setMapView(mapView : MapView) {
        this.mapView = mapView
    }

    fun getPlacesClient() : PlacesClient {
        return this.placesClient
    }

    fun setPlacesClient(placesClient : PlacesClient) {
        this.placesClient = placesClient
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


    fun inizializeClusterManager() : ClusterManager<PostMarkerItem> {

        this.mClusterManager = ClusterManager(context, this.mMap)
        this.mClusterManager!!.renderer = ClusterMarkerRenderer(context, this.mMap, this.mClusterManager)

        this.mMap!!.setOnCameraIdleListener(mClusterManager)

        mClusterManager!!.markerCollection.setInfoWindowAdapter(context?.let {
            CustomInfoWindowAdapter(
                it
            )
        })

        mClusterManager!!.markerCollection.setOnInfoWindowClickListener { marker: Marker ->
            //quando si clicca sulla window del marker
        }


        readItems()

        return mClusterManager as ClusterManager<PostMarkerItem>

    }

    private fun readItems() {

        //if (PostData.postMarkerList.none())
        PostData.resetPostMarkerList()
        PostData.createPostMarkerList()

        Log.i("MapFragmentCompose", "PostData.postMarkerList: {${PostData.postMarkerList.toString()}")

        for (item in PostData.postMarkerList) {
            var offset = Random.nextInt(0, 10)/1000.toDouble()
            var position : LatLng = item.getPosition()
            var lat = position.latitude + offset
            var lng = position.longitude + offset
            var title = item.getTitle()
            var img = item.getImagePath()
            var user = item.getUser()
            val offsetItem = PostMarkerItem(lat, lng, title!!, img!!, user!!)
            mClusterManager!!.addItem(offsetItem)
        }

        /**
        val items: ArrayList<PostMarkerItem> = ArrayList()

        val vienna = PostMarkerItem(48.2050491798, 16.3701485194, "Vienna", "android.resource://com.educ.unito.myapplication/drawable/torino", "mario.rossi")
        items.add(vienna)

        val sydney = PostMarkerItem(-33.87365, 151.20689, "Sydney", "sydney.png", "mario.rossi")
        items.add(sydney)

        val sydney2 = PostMarkerItem(-33.87365, 151.20689, "Sydney", "torino.png", "mario.rossi")
        items.add(sydney2)
        items.add(sydney2)
        items.add(sydney2)

        val brisbane = PostMarkerItem(
            -27.47093, 153.0235,
            "Brisbane",
            "milano.png",
            "mario.rossi"
        )
        items.add(brisbane)

        val perth = PostMarkerItem(
            -31.952854, 115.857342,
            "Perth",
            "roma.png",
            "mario.rossi"
        )
        items.add(perth)

        for (item in items) {
            var offset = Random.nextInt(0, 10)/1000.toDouble()
            var position : LatLng = item.getPosition()
            var lat = position.latitude + offset
            var lng = position.longitude + offset
            var title = item.getTitle()
            var img = item.getImagePath()
            var user = item.getUser()
            val offsetItem = PostMarkerItem(lat, lng, title!!, img!!, user!!)
            mClusterManager!!.addItem(offsetItem)
        }
        */

    }

    /**
     * Funzione che richiede l'accesso alla posizione GPS corrente
     */
    fun getLocationPermission(myContext : MainActivity) {

        //while (!locationPermissionGranted) {
            if (ContextCompat.checkSelfPermission(
                    myContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            } else {
                ActivityCompat.requestPermissions(
                    myContext, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        //}
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    public fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap!!.isMyLocationEnabled = false
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission(this.activity as MainActivity)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    public fun getDeviceLocation() {
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
                            mMap!!.isMyLocationEnabled = true
                            mMap!!.uiSettings.isMyLocationButtonEnabled = true
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                            mMap!!.setOnMyLocationButtonClickListener(this)
                        }
                    } else {
                        Log.d(ContentValues.TAG, "Current location is null. Using defaults.")
                        Log.e(ContentValues.TAG, "Exception: %s", task.exception)
                        mMap!!.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap!!.isMyLocationEnabled = false
                        mMap!!.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    /**
     * Sposta il button relativo alla geolocalizzazione in basso a destra
     */
    public fun moveCurrentLocationButtonOnBottom() {
        val locationButton = (mapView!!.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp =  locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)
    }

    /**
    @SuppressLint("PotentialBehaviorOverride")
    public fun addMarkers() {
        mMap!!.setOnMarkerClickListener(this)

        val sydney = LatLng(-33.87365, 151.20689)
        val brisbane = LatLng(-27.47093, 153.0235)
        val perth = LatLng(-31.952854, 115.857342)

        latlngs.add(sydney)
        latlngs.add(brisbane)
        latlngs.add(perth)

        for (point in latlngs) {
            options.position(point).title("Città")
            mMap!!.addMarker(options)?.tag = 0
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
        }
    }
    */

    /**
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
    */


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



    private class ClusterMarkerRenderer(
            context: Context?,
            map: GoogleMap?,
            clusterManager: ClusterManager<PostMarkerItem>?
    ) :
            DefaultClusterRenderer<PostMarkerItem>(context, map, clusterManager) {


        override fun onBeforeClusterItemRendered(item: PostMarkerItem, markerOptions: MarkerOptions) {
            /**
            if (item.getIcon() != null) {
            markerOptions.icon(item.getIcon())
            }
            if (!item.hasCustomBitmap()) {
            markerOptions.rotation(item.getRotation())
            }
             */
            var currentUser = "mario.rossi"

            if (Amplify.Auth.currentUser != null)
                currentUser = Amplify.Auth.currentUser.username

            if (item.getUser() == currentUser) {
                markerOptions
                    .title(item.title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            }
            else {
                markerOptions
                    .title(item.title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
            /**
            markerOptions.anchor(0.5f, 0.5f)
            markerOptions.flat(true)
            markerOptions.infoWindowAnchor(0.5f, 0.5f)
            markerOptions.snippet(
            item.getImei().toString() + "#" + item.getPosition().latitude
            + "#" + item.getPosition().longitude + "#" + item.getRotation() + "#" + item.getIconName() + "#" + item.getName()
            )
             */
            super.onBeforeClusterItemRendered(item, markerOptions)
        }

        override fun onClusterItemRendered(item: PostMarkerItem, marker: Marker) {
            super.onClusterItemRendered(item, marker)
            marker.tag = item
        }
    }

}


@SuppressLint("PotentialBehaviorOverride")
@Composable
fun MapViewContainer(map: MapView, fragment : MapFragmentCompose) {

    LaunchedEffect(map) {
        val googleMap = map.awaitMap()
    }

    val coroutineScope = rememberCoroutineScope()

    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView({ map }) { mapView ->
                coroutineScope.launch {
                    val googleMap = mapView.awaitMap()

                    fragment.setGoogleMap(googleMap)
                    fragment.setMapView(mapView)

                    //permessi per geolocalizzazione
                    fragment.getLocationPermission(fragment.activity as MainActivity)
                    fragment.updateLocationUI()
                    fragment.getDeviceLocation()

                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition))
                    googleMap.uiSettings.isZoomControlsEnabled = false  //zoom
                    googleMap.uiSettings.isCompassEnabled = false       //bussola
                    googleMap.uiSettings.isMapToolbarEnabled = false    //indicazioni stradali

                    fragment.moveCurrentLocationButtonOnBottom()


                    val clusterManager = fragment.inizializeClusterManager()

                    clusterManager.setOnClusterItemClickListener { item: PostMarkerItem ->
                        Log.i("MapViewContainer: ", "clusterManager.setOnCusterItemClickListener")
                        Log.i("MapViewContainer: ", "$item")
                        //ComposeView(fragment.requireContext()).apply {
                        //    setContent {
                        //        PostMarkerPopup(post = item)
                        //    }
                        //}
                        //true
                        false
                    }

                    clusterManager.setOnClusterClickListener { item: Cluster<PostMarkerItem?>? ->
                        Log.i("MapViewContainer: ", "clusterManager.setOnCusterClickListener")
                        Log.i("MapViewContainer: ", "$item")

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(item!!.position,
                            Math.floor((googleMap.cameraPosition.zoom+1).toDouble()).toFloat()
                        ), 300, null)
                        true

                        //true
                        //false
                    }
                    //googleMap.setOnMarkerClickListener(this)

                }
            }
        }
    }
}




/**
@Composable
fun PostMarkerPopup (post : PostMarkerItem) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
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
            Text(
                text = post.getUser(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(2.dp)
            )


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
        Spacer(modifier = Modifier.size(2.dp))

        Row(
            modifier = Modifier.absolutePadding(top = 1.dp, bottom = 1.dp, right = 0.dp, left = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.LocationOn,
                contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = post.getTitle(),
                //fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(2.dp)
            )
        }

        //Image
        Spacer(modifier = Modifier.size(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = File(post.getImagePath())),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }

        //Title
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = post.getTitle(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(2.dp)
        )

        //Description
        /**
        Text(
            text = post.getDescription(),
            fontSize = 16.sp,
            modifier = Modifier.padding(2.dp)
        )
        */

    }

}
*/