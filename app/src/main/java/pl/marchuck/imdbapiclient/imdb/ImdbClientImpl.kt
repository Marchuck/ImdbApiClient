package pl.marchuck.imdbapiclient.imdb

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import pl.marchuck.imdbapiclient.ui.detail.usecase.MovieDetailsResult
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.Exception
import kotlin.jvm.Throws


interface ImdbClient {

    suspend fun search(type: SearchResultType, query: String): SearchResponse

    @Throws(KnownIssue.ApiLimitException::class)
    suspend fun getMovieDetail(id: String): MovieResponse
}

class ImdbClientImpl constructor(
    private val context: Context,
    private val config: ImdbConfig
) : ImdbClient {

    private val api by lazy {
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        ChuckerInterceptor.Builder(context)
                            .alwaysReadResponseBody(true)
                            .build()
                    )
                    .build()
            )
            .baseUrl(config.endpointUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImdbApi::class.java)
    }

    override suspend fun search(type: SearchResultType, query: String): SearchResponse {
        try {
            val response = api.search(type.key, config.apiKey, query)
            if (response.errorMessage.isApiLimit()) {
                throw KnownIssue.ApiLimitException
            }
            return response
        } catch (e: Throwable) {
            handleError(e)
        }
    }

    override suspend fun getMovieDetail(id: String): MovieResponse {
        try {
            val response = api.movieDetail(
                config.apiKey,
                id,
                options = "FullActor,FullCast,Images,Trailer,Ratings"
            )
            if (response.errorMessage.isApiLimit()) {
                throw KnownIssue.ApiLimitException
            }
            return response
        } catch (e: Throwable) {
            handleError(e)
        }
    }

    private fun handleError(e: Throwable): Nothing = when (e) {
        is HttpException -> {
            throw KnownIssue.ApiException(e.code())
        }
        is IOException -> {
            throw KnownIssue.NetworkException(e)
        }
        else -> throw e
    }

    private fun String?.isApiLimit(): Boolean {
        return this.orEmpty().contains("Maximum usage")
    }
}

sealed class KnownIssue(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    object ApiLimitException : KnownIssue()

    data class ApiException(val code: Int) : KnownIssue("errorCode=$code")

    data class NetworkException(val issue: Throwable) : KnownIssue(cause = issue)
}
