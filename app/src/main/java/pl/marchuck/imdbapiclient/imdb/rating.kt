package pl.marchuck.imdbapiclient.imdb

import com.google.gson.annotations.SerializedName

data class RatingResponse(
    @SerializedName("imDbId") val imdbId: String,
    @SerializedName("title") val title: String,
    @SerializedName("year") val year: String,
    @SerializedName("imDb") val imdbRating: String,
    @SerializedName("metacritic") val metacriticRating: String,
    @SerializedName("theMovieDb") val theMovieDb: String,
    @SerializedName("rottenTomatoes") val rottenTomatoes: String,
    @SerializedName("tV_com") val tVComRating: String,
    @SerializedName("filmAffinity") val filmAffinity: String,
)
