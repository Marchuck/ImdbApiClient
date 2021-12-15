package pl.marchuck.imdbapiclient.ui.detail

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import pl.marchuck.imdbapiclient.ui.detail.usecase.GetMovieDetailsUseCase
import pl.marchuck.imdbapiclient.ui.detail.usecase.MovieDetails
import pl.marchuck.imdbapiclient.ui.detail.usecase.MovieDetailsResult
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class MovieDetailViewModelTest {

    private val movieId = "100"

    lateinit var viewModel: MovieDetailViewModel

    val useCase: GetMovieDetailsUseCase = mock()

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MovieDetailViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when screen initialized, it loads and displays movie details`() = runBlocking {
        val details = MovieDetails(
            "boring movie",
            emptyList(),
            emptyList(),
            null
        )
        whenever(useCase.execute(movieId)).thenReturn(MovieDetailsResult.Success(details))

        viewModel.viewState.test {
            assertEquals(MovieResponseState.Loading, expectItem())

            viewModel.onEvent(MovieDetailEvent.Initialize(movieId))
            assertEquals(MovieResponseState.Ready(details), expectItem())

            expectNoEvents()
        }
    }

    @Test
    fun `when trailer button clicked, launches trailer`() = runBlocking {
        val url = "path/to/trailer"
        viewModel.sideEffects.test {
            viewModel.onEvent(MovieDetailEvent.PlayTrailer(url))
            assertEquals(MovieDetailSideEffect.BrowseTrailer(url), expectItem())
        }
    }

    @Test
    fun `when screen initialized, it loads and displays error`() = runBlocking {
        whenever(useCase.execute(movieId)).thenReturn(MovieDetailsResult.Error.NetworkIssue)

        viewModel.viewState.test {
            assertEquals(MovieResponseState.Loading, expectItem())

            viewModel.onEvent(MovieDetailEvent.Initialize(movieId))
            assertEquals(MovieResponseState.FailedToFetch, expectItem())

            expectNoEvents()
        }
    }
}