package pl.marchuck.imdbapiclient.ui.list.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import pl.marchuck.imdbapiclient.imdb.ImdbClient
import pl.marchuck.imdbapiclient.imdb.SearchResult
import pl.marchuck.imdbapiclient.imdb.SearchResultType
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractor.Companion.DEBOUNCE_MILLIS
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractor.Companion.MIN_QUERY_LENGTH

interface SearchMoviesInteractor {

    companion object {
        const val MIN_QUERY_LENGTH = 3
        const val DEBOUNCE_MILLIS = 300L
    }

    fun setQuery(newQuery: String)

    fun searchChanges(): Flow<List<SearchResult>>
}

class SearchMoviesInteractorImpl(
    private val client: ImdbClient
) : SearchMoviesInteractor {

    private val query = MutableStateFlow("")

    override fun setQuery(newQuery: String) {
        val query = if (newQuery.length < MIN_QUERY_LENGTH) {
            ""
        } else {
            newQuery
        }
        this.query.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchChanges(): Flow<List<SearchResult>> {
        return query
            .filter { it.isNotBlank() }
            .debounce(DEBOUNCE_MILLIS)
            .mapLatest { query ->
                client.search(SearchResultType.Movie, query).results.orEmpty()
            }
    }
}
