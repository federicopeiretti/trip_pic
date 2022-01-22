package educ.unito.myapplication.search

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import educ.unito.myapplication.MainActivity
import educ.unito.myapplication.profile.ProfileViewModel
import educ.unito.myapplication.profile.ui.theme.ProfileUITheme

class SearchFragment : Fragment() {

    // riferimento alla Main Activity
    private var myContext: MainActivity? = null


    companion object {
        fun newInstance() = SearchFragment()
    }

    //private lateinit var viewModel: SearchViewModel
    private val viewModel by viewModels<SearchViewModel>()

    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            setContent {
                ProfileUITheme {
                    SearchScreen()
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
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