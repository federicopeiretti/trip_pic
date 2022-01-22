package educ.unito.myapplication.add

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import educ.unito.myapplication.MainActivity
import educ.unito.myapplication.R
import educ.unito.myapplication.profile.ui.theme.ProfileUITheme
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.listeners.IPickResult
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import educ.unito.myapplication.databinding.AddFragmentBinding
import educ.unito.myapplication.profile.CompleteProfileViewModel
import kotlinx.android.synthetic.main.add_fragment.view.*


class AddFragment : Fragment() {

    private var addView : View? = null

    // riferimento alla Main Activity
    private var myContext: MainActivity? = null

    // The entry point to the Places API
    private lateinit var placesClient: PlacesClient


    companion object {
        fun newInstance() = AddFragment()
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private val viewModel by viewModels<AddViewModel>()

    // BINDING
    private var _binding: AddFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = AddFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        view.add_compose.setContent {
            ProfileUITheme {
                AddScreen(requireActivity() as MainActivity, viewModel = viewModel)
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
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        autocompleteFragment.setHint("Luogo")
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
                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage!!)
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