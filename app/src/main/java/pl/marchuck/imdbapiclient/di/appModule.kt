package pl.marchuck.imdbapiclient.di


import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.marchuck.imdbapiclient.BuildConfig
import pl.marchuck.imdbapiclient.common.NightModeWrapper
import pl.marchuck.imdbapiclient.imdb.ImdbClient
import pl.marchuck.imdbapiclient.imdb.ImdbClientImpl
import pl.marchuck.imdbapiclient.imdb.ImdbConfig
import pl.marchuck.imdbapiclient.ui.detail.MovieDetailViewModel
import pl.marchuck.imdbapiclient.ui.detail.usecase.GetMovieDetailsUseCase
import pl.marchuck.imdbapiclient.ui.list.MovieListViewModel
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractor
import pl.marchuck.imdbapiclient.ui.list.interactor.SearchMoviesInteractorImpl


val appModule = module {

    factory { parameters -> NightModeWrapper(parameters.get()) }

    single<ImdbConfig> { ImdbConfig.Factory(get()).create(BuildConfig.IMDB_API_KEY) }

    single<ImdbClient> { ImdbClientImpl(get(), get()) }

    single<SearchMoviesInteractor> { SearchMoviesInteractorImpl(get()) }

    factory { GetMovieDetailsUseCase(get()) }

    viewModel { MovieListViewModel(get()) }

    viewModel { MovieDetailViewModel(get()) }
}
