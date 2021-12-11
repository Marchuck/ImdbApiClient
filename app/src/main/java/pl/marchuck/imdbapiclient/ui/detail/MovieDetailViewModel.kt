package pl.marchuck.imdbapiclient.ui.detail

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import pl.marchuck.imdbapiclient.common.AbstractViewModel
import pl.marchuck.imdbapiclient.imdb.ImdbClient
import pl.marchuck.imdbapiclient.imdb.MovieResponse

class MovieDetailViewModel constructor(
    private val clientImpl: ImdbClient
) : AbstractViewModel<MovieResponseState, MovieDetailSideEffect>(
    initialState = MovieResponseState.Loading
) {

    fun initialize(movieId: String) {
        viewModelScope.launch {
            val newState = try {
                val response = clientImpl.getMovieDetail(movieId)
                MovieResponseState.Ready(response)
            } catch (c: CancellationException) {
                MovieResponseState.Loading
            } catch (e: Exception) {
                MovieResponseState.FailedToFetch
            }
            pushState { newState }
        }
    }
}


sealed class MovieResponseState {
    data class Ready(val response: MovieResponse) : MovieResponseState()
    object Loading : MovieResponseState()
    object FailedToFetch : MovieResponseState()
}

sealed class MovieDetailSideEffect {
    object Back : MovieDetailSideEffect()
}