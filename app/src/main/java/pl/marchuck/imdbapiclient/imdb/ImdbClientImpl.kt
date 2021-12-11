package pl.marchuck.imdbapiclient.imdb

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface ImdbClient {

    suspend fun search(type: SearchResultType, query: String): SearchResponse

    suspend fun getMovieDetail(id: String): MovieResponse

    suspend fun getImages(id: String): ImagesResponse
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
        return api.search(type.key, config.apiKey, query)
    }

    override suspend fun getMovieDetail(id: String): MovieResponse {
        return api.movieDetail(
            config.apiKey,
            id,
            options = "FullActor,FullCast,Images,Trailer,Ratings"
        )
    }

    override suspend fun getImages(id: String): ImagesResponse {
        return api.images(config.apiKey, id)
    }
}