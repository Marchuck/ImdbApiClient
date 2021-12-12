package pl.marchuck.imdbapiclient.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.marchuck.imdbapiclient.R
import pl.marchuck.imdbapiclient.common.autoCleared
import pl.marchuck.imdbapiclient.common.hideKeyboard
import pl.marchuck.imdbapiclient.databinding.FragmentSearchBinding
import pl.marchuck.imdbapiclient.databinding.ItemToolbarBinding
import pl.marchuck.imdbapiclient.ui.MainActivity
import pl.marchuck.imdbapiclient.ui.detail.MovieDetailFragment
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractorImpl

class MovieListFragment : Fragment() {

    private var binding by autoCleared<FragmentSearchBinding>()

    private val adapter = SearchAdapter()

    private val navigation by lazy { (requireActivity() as MainActivity).navigationWrapper }

    private val viewModel: MovieListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar(binding.toolbar)

        configureRecyclerView(binding.recyclerView)


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.onEvent(MovieListEvent.QueryChange(query.orEmpty()))
                return true
            }
        })
        viewModel.viewState.asLiveData().observe(viewLifecycleOwner, ::renderViewState)
        viewModel.sideEffects.asLiveData().observe(viewLifecycleOwner, ::handleSideEffect)

        viewModel.initialize()
    }

    private fun configureRecyclerView(recyclerView: RecyclerView) {
        adapter.listener = {
            viewModel.onEvent(MovieListEvent.EntryClicked(it))
        }
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                hideKeyboard()
            }
        })
    }

    private fun configureToolbar(toolbar: ItemToolbarBinding) {
        toolbar.title.text = getString(R.string.app_name)
        val nightModeWrapper = (requireActivity() as MainActivity).nighModeWrapper
        toolbar.actionIcon.setImageResource(nightModeWrapper.darkModeIcon())
        toolbar.actionIcon.isVisible = true
        toolbar.actionIcon.setOnClickListener {
            nightModeWrapper.toggleDarkMode()
            //viewModel.onEvent(MovieListEvent.OpenSettings)
        }
    }

    private fun handleSideEffect(effect: MovieListSideEffect?) = when (effect) {
        is MovieListSideEffect.BrowseMovie -> {
            navigation.pushScreen(
                MovieDetailFragment.newInstance(
                    effect.movieId,
                    effect.movieName
                ),
                "movie-detail"
            )
        }
        null -> Unit
    }

    private fun renderViewState(state: MovieListViewState) {
        adapter.submitList(state.items)

        binding.noResults.isVisible = state.contentState in listOf(
            ContentState.Error,
            ContentState.NoResults,
            ContentState.ApiLimit,
        )
        binding.progressbar.isVisible = state.contentState is ContentState.Loading
        return when (state.contentState) {
            ContentState.Error -> {
                binding.noResultsText.setText(R.string.imdb__something_went_wrong)
            }
            ContentState.Idle -> Unit
            ContentState.Loading -> Unit
            ContentState.NoResults -> {
                binding.noResultsText.setText(R.string.imdb__search_no_results)
            }
            ContentState.ApiLimit -> {
                binding.noResultsText.setText(R.string.imdb__search_api_limit)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): MovieListFragment {
            return MovieListFragment()
        }
    }
}
