package pl.marchuck.imdbapiclient.imdb

import com.google.gson.annotations.SerializedName

data class ImagesResponse(
    @SerializedName("imDbId") val id: String,
    @SerializedName("items") val items: List<ImageItem>
)

data class ImageItem(
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String
)
