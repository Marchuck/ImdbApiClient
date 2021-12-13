package pl.marchuck.imdbapiclient.imdb

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("awards") val awards: String?,
    @SerializedName("fullCast") val fullCast: FullCast?,
    @SerializedName("imDbRating") val rating: String?,
    @SerializedName("trailer") val trailer: TrailerResponse?,
    @SerializedName("errorMessage") val errorMessage: String?,
    @SerializedName("images") val images: ImagesResponse?,
    @SerializedName("keywords") val keywords: String? //separatedByComma
)

data class FullCast(
    @SerializedName("directors") val directors: JobInfo?,
    @SerializedName("writers") val writers: JobInfo?,
    @SerializedName("actors") val actors: List<Actor>?,
    @SerializedName("genres") val genres: String? //separatedByComma
)

data class JobInfo(
    @SerializedName("job") val job: String,
    @SerializedName("items") val items: List<Human>
)

data class Human(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class Actor(
    @SerializedName("id") val id: String,
    @SerializedName("image") val image: String,
    @SerializedName("name") val name: String,
    @SerializedName("asCharacter") val asCharacter: String
)
