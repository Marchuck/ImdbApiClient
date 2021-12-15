package pl.marchuck.imdbapiclient.ui.list

import app.cash.turbine.test
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.marchuck.imdbapiclient.imdb.SearchResult
import pl.marchuck.imdbapiclient.imdb.SearchResultType
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractor
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class MovieListViewModelTest {


    lateinit var viewModel: MovieListViewModel

    private val interactor = SearchMoviesInteractorMock()

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MovieListViewModel(interactor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when user enters at least 3 letters, search is triggered`() = runBlocking {
        val ironManResults = listOf(searchResultOf("1"), searchResultOf("2"), searchResultOf("3"))
        val ironMan2Results = listOf(searchResultOf("1"), searchResultOf("2"))

        interactor.putStubs(
            "iron man" to FakeResult.Success(ironManResults),
            "iron man 2" to FakeResult.Success(ironMan2Results),
        )

        viewModel.viewState.test {
            assertEquals(
                MovieListViewState(
                    "",
                    emptyList(),
                    contentState = ContentState.Idle
                ), expectItem()
            )
            viewModel.onEvent(MovieListEvent.Initialize)
            viewModel.onEvent(MovieListEvent.QueryChange("iron man"))
            assertEquals(
                MovieListViewState(
                    "iron man",
                    emptyList(),
                    contentState = ContentState.Loading
                ),
                expectItem()
            )
            assertEquals(
                MovieListViewState(
                    "iron man",
                    ironManResults,
                    contentState = ContentState.Idle
                ), expectItem()
            )

            viewModel.onEvent(MovieListEvent.QueryChange("iron man 2"))

            assertEquals(
                MovieListViewState(
                    "iron man 2",
                    ironManResults,
                    contentState = ContentState.Loading
                ),
                expectItem()
            )
            assertEquals(
                MovieListViewState(
                    "iron man 2",
                    ironMan2Results,
                    contentState = ContentState.Idle
                ), expectItem()
            )
        }
    }

    private fun searchResultOf(id: String): SearchResult {
        return SearchResult(
            id = id,
            resultType = SearchResultType.Movie,
            imageUrl = "path/to/image",
            title = "Movie title",
            description = ""
        )
    }
}

private class SearchMoviesInteractorMock : SearchMoviesInteractor {
    private val fakes = hashMapOf<String, FakeResult>()
    private val queryFlow = MutableStateFlow("")

    override fun setQuery(newQuery: String) {
        queryFlow.value = newQuery
    }

    override fun searchChanges(): Flow<List<SearchResult>> {
        return queryFlow
            .filter { it.length > 3 }
            .map { query ->
                when (val item = requireNotNull(fakes[query]) {
                    "no fakes provided for query \'$query\'"
                }) {
                    is FakeResult.Failure -> throw item.issue
                    is FakeResult.Success -> item.items
                }
            }
    }

    fun putStubs(vararg args: Pair<String, FakeResult>) {
        args.forEach { (newQuery, newResult) ->
            fakes[newQuery] = newResult
        }
    }
}

private sealed class FakeResult {
    data class Success(val items: List<SearchResult>) : FakeResult()
    data class Failure(val issue: Throwable) : FakeResult()
}
