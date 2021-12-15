package pl.marchuck.imdbapiclient.ui.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.marchuck.imdbapiclient.common.AbstractViewModel
import pl.marchuck.imdbapiclient.imdb.KnownIssue
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

    fun onEvent(event: MovieListEvent) = when (event) {
        MovieListEvent.Initialize -> initialize()
        is MovieListEvent.QueryChange -> onQueryChanged(event.newQuery)
        is MovieListEvent.EntryClicked -> pushSideEffect(
            MovieListSideEffect.BrowseMovie(event.entry.id, event.entry.title)
        )
        MovieListEvent.ToggleDarkMode -> pushSideEffect(
            MovieListSideEffect.ToggleDarkMode
        )
    }

    private fun initialize() {
        viewModelScope.launch {
            try {
                searchMoviesInteractor
                    .searchChanges()
                    .collect { results ->
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
                val contentState = if (e is KnownIssue.ApiLimitException) {
                    ContentState.ApiLimit
                } else {
                    if (e !is KnownIssue) {
                        Timber.e(SearchMoviesException(e))
                    }
                    ContentState.Error
                }
                pushState {
                    it.copy(
                        items = emptyList(),
                        contentState = contentState
                    )
                }
            }
        }
    }

    private fun onQueryChanged(newQuery: String) {
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
}

sealed class MovieListEvent {
    data class QueryChange(val newQuery: String) : MovieListEvent()
    data class EntryClicked(val entry: SearchResult) : MovieListEvent()
    object ToggleDarkMode : MovieListEvent()
    object Initialize : MovieListEvent()
}

sealed class MovieListSideEffect {
    data class BrowseMovie(val movieId: String, val movieName: String) : MovieListSideEffect()
    object ToggleDarkMode : MovieListSideEffect()
}

data class MovieListViewState(
    val query: String = "",
    val items: List<SearchResult> = emptyList(),
    val contentState: ContentState = ContentState.Idle
)

enum class ContentState {
    Loading,
    Idle,
    NoResults,
    Error,
    ApiLimit,
}

class SearchMoviesException(cause: Exception) : java.lang.Exception(cause)
