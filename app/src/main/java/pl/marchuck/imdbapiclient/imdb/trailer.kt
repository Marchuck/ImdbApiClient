package pl.marchuck.imdbapiclient.imdb

import com.google.gson.annotations.SerializedName

data class TrailerResponse(
    @SerializedName("imDbId") val movieId: String,
    @SerializedName("videoId") val videoId: String,
    @SerializedName("videoTitle") val title: String,
    @SerializedName("videoDescription") val description: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String,
    @SerializedName("link") val videoUrl: String?
)
