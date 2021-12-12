package pl.marchuck.imdbapiclient.ui.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.marchuck.imdbapiclient.common.AbstractViewModel
import pl.marchuck.imdbapiclient.imdb.KnownIssues
import pl.marchuck.imdbapiclient.imdb.SearchResult
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractor
import timber.log.Timber

class MovieListViewModel constructor(
    private val searchMoviesInteractor: SearchMoviesInteractor
) : AbstractViewModel<MovieListViewState, MovieListSideEffect>(
    initialState = MovieListViewState(
        query = "",
        items = emptyList(),
        contentState = ContentState.Idle
    )
) {

    fun initialize() {
        viewModelScope.launch {
            try {
                searchMoviesInteractor
                    .searchChanges()
                    .collect { results ->
                        Timber.w("results ${results.size}")

                        pushState {
                            it.copy(
                                items = results,
                                contentState = if (results.isEmpty()) {
                                    ContentState.NoResults
                                } else {
                                    ContentState.Idle
                                }
                            )
                        }
                    }
            } catch (e: Exception) {
                val contentState = if (e is KnownIssues.ApiLimitException) {
                    ContentState.ApiLimit
                } else {
                    ContentState.Error
                }
                Timber.w(e, "results fetch error")

                pushState {
                    it.copy(
                        items = emptyList(),
                        contentState = contentState
                    )
                }
            }
        }
    }

    fun onEvent(event: MovieListEvent) = when (event) {
        is MovieListEvent.EntryClicked -> {
            pushSideEffect(
                MovieListSideEffect.BrowseMovie(event.entry.id, event.entry.title)
            )
        }
        is MovieListEvent.QueryChange -> {
            val newQuery = event.newQuery
            pushState {
                it.copy(
                    query = newQuery,
                    contentState = if (newQuery.length > SearchMoviesInteractor.MIN_QUERY_LENGTH) {
                        ContentState.Loading
                    } else {
                        ContentState.Idle
                    }
                )
            }
            searchMoviesInteractor.setQuery(newQuery)
        }
        MovieListEvent.ShowFilters -> Unit
        MovieListEvent.OpenSettings -> {
            //todo: display settings
        }
    }
}

sealed class MovieListEvent {
    data class QueryChange(val newQuery: String) : MovieListEvent()
    data class EntryClicked(val entry: SearchResult) : MovieListEvent()
    object ShowFilters : MovieListEvent()
    object OpenSettings : MovieListEvent()
}

sealed class MovieListSideEffect {
    data class BrowseMovie(val movieId: String, val movieName: String) : MovieListSideEffect()
}

data class MovieListViewState(
    val query: String = "",
    val items: List<SearchResult> = emptyList(),
    val contentState: ContentState = ContentState.Idle
)

sealed class ContentState {
    object Loading : ContentState()
    object Idle : ContentState()
    object NoResults : ContentState()
    object Error : ContentState()
    object ApiLimit : ContentState()
}
