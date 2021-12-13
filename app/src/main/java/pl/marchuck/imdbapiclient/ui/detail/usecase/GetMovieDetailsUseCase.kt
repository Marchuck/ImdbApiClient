package pl.marchuck.imdbapiclient.ui.detail.usecase

import org.intellij.lang.annotations.Language
import pl.marchuck.imdbapiclient.imdb.*
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class GetMovieDetailsUseCase(private val clientImpl: ImdbClient) {
    suspend fun execute(movieId: String): MovieDetailsResult {
        try {
            val response = clientImpl.getMovieDetail(movieId)

            val details = MovieDetails(
                metadata = buildMetadata(response),
                images = response.images?.items.orEmpty(),
                actors = response.fullCast?.actors.orEmpty(),
                trailerUrls = buildTrailer(response.trailer)
            )
            return MovieDetailsResult.Success(details)
        } catch (e: Exception) {
            return when (e) {
                is HttpException -> {
                    MovieDetailsResult.Error.ApiIssue(e.code())
                }
                is IOException -> {
                    MovieDetailsResult.Error.NetworkIssue
                }
                is KnownIssues.ApiLimitException -> {
                    MovieDetailsResult.Error.ApiLimit
                }
                else -> {
                    MovieDetailsResult.Error.InternalIssue(e)
                }
            }
        }
    }

    @Language("HTML")
    fun buildMetadata(response: MovieResponse): String {
        val ratingLine = response.rating.orEmpty()
            .withNewlineIfNotEmpty(prefix = "<b>IMDB rating</b>: ")

        val directorsLine = formatJobInfo(response.fullCast?.directors)
            .withNewlineIfNotEmpty()

        val writersLine = formatJobInfo(response.fullCast?.writers)
            .withNewlineIfNotEmpty()

        val plotLine = response.plot.orEmpty().withNewlineIfNotEmpty()

        return ratingLine + directorsLine + writersLine + plotLine
    }

    private fun String.withNewlineIfNotEmpty(prefix: String = ""): String {
        return if (isNotEmpty()) {
            "$prefix$this<br/>"
        } else {
            this
        }
    }

    private fun buildTrailer(trailerResponse: TrailerResponse?): TrailerUrls? {
        val url = trailerResponse?.videoUrl
        if (url == null) {
            return null
        } else {
            return TrailerUrls(url, trailerResponse.thumbnailUrl)
        }
    }


    private fun formatJobInfo(jobInfo: JobInfo?): String {
        if (jobInfo == null) return ""
        val job = jobInfo.job
        val items = jobInfo.items
        //todo: i18n
        val label = when {
            items.isEmpty() -> {
                return ""
            }
            items.size > 1 -> {
                job + "s"
            }
            else -> {
                job
            }
        }
        return items.joinToString(prefix = "$label: ") { it.name }
    }
}

sealed class MovieDetailsResult {
    data class Success(val details: MovieDetails) : MovieDetailsResult()
    sealed class Error : MovieDetailsResult() {
        object NetworkIssue : Error()
        object ApiLimit : Error()
        data class ApiIssue(val code: Int) : Error()
        data class InternalIssue(val cause: Throwable) : Error()
    }
}

data class MovieDetails(
    @Language("HTML")
    val metadata: String,
    val images: List<ImageItem>,
    val actors: List<Actor>,
    val trailerUrls: TrailerUrls?
)

data class TrailerUrls(val video: String, val thumbnail: String)
