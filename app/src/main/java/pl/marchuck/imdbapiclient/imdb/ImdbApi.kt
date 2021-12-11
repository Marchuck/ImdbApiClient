package pl.marchuck.imdbapiclient.imdb

import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 * @see https://imdb-api.com/swagger/index.html
 */
interface ImdbApi {

    @GET("Search{searchType}/{apiKey}/{query}")
    suspend fun search(
        @Path("searchType") searchType: String,
        @Path("apiKey") apikey: String,
        @Path("query") query: String
    ): SearchResponse

    @GET("Title/{apiKey}/{id}/{options}")
    suspend fun movieDetail(
        @Path("apiKey") apikey: String,
        @Path("id") id: String,
        @Path("options") options: String,
    ): MovieResponse

    @GET("Images/{apiKey}/{id}")
    suspend fun images(
        @Path("apiKey") apikey: String,
        @Path("id") id: String
    ): ImagesResponse
}