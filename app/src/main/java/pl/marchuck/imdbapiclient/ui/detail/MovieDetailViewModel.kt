package pl.marchuck.imdbapiclient.ui.detail

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.marchuck.imdbapiclient.common.AbstractViewModel
import pl.marchuck.imdbapiclient.ui.detail.usecase.GetMovieDetailsUseCase
import pl.marchuck.imdbapiclient.ui.detail.usecase.MovieDetails
import pl.marchuck.imdbapiclient.ui.detail.usecase.MovieDetailsResult

class MovieDetailViewModel constructor(
    private val useCase: GetMovieDetailsUseCase
) : AbstractViewModel<MovieResponseState, MovieDetailSideEffect>(
    initialState = MovieResponseState.Loading
) {
    fun onEvent(event: MovieDetailEvent) = when (event) {
        MovieDetailEvent.BackPressed -> pushSideEffect(
            MovieDetailSideEffect.Back
        )
        is MovieDetailEvent.BrowseTrailer -> pushSideEffect(
            MovieDetailSideEffect.BrowseTrailer(event.trailerUrl)
        )
        is MovieDetailEvent.Initialize -> initialize(event.movieId)
    }

    private fun initialize(movieId: String) {
        viewModelScope.launch {
            val newState = when (val result = useCase.execute(movieId)) {
                MovieDetailsResult.Error.ApiLimit -> MovieResponseState.ApiLimit
                is MovieDetailsResult.Error.ApiIssue,
                is MovieDetailsResult.Error.InternalIssue,
                MovieDetailsResult.Error.NetworkIssue -> MovieResponseState.FailedToFetch
                is MovieDetailsResult.Success -> {
                    MovieResponseState.Ready(result.details)
                }
            }
            pushState { newState }
        }
    }
}

sealed class MovieResponseState {
    data class Ready(val details: MovieDetails) : MovieResponseState()
    object Loading : MovieResponseState()
    object FailedToFetch : MovieResponseState()
    object ApiLimit : MovieResponseState()
}

sealed class MovieDetailEvent {
    data class Initialize(val movieId: String) : MovieDetailEvent()
    data class BrowseTrailer(val trailerUrl: String) : MovieDetailEvent()
    object BackPressed : MovieDetailEvent()
}

sealed class MovieDetailSideEffect {
    data class BrowseTrailer(val trailerUrl: String) : MovieDetailSideEffect()
    object Back : MovieDetailSideEffect()
}