package educ.unito.myapplication.favorites

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import educ.unito.myapplication.MainActivity
import educ.unito.myapplication.profile.ui.theme.ProfileUITheme

class FavoritesFragment : Fragment() {

    // riferimento alla Main Activity
    private var myContext: MainActivity? = null


    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private lateinit var viewModel: FavoritesViewModel

    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            setContent {
                ProfileUITheme {
                    FavouriteScreen()
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
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