package pl.marchuck.imdbapiclient.imdb

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.util.*

data class SearchResponse(
    @SerializedName("expression") val expression: String,
    @SerializedName("results") val results: List<SearchResult>
)

data class SearchResult(
    @SerializedName("id") val id: String,
    @JsonAdapter(SearchResultType.Adapter::class)
    @SerializedName("resultType") val resultType: SearchResultType,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)

enum class SearchResultType(val key: String) {
    Title("Title"),
    Movie("Movie"),
    Series("Series"),
    Name("Name"),
    Episode("Episode"),
    Company("Company"),
    Keyword("Keyword"),
    All("All");

    companion object {
        @JvmStatic
        fun resolve(key: String): SearchResultType? {
            return values().find { it.key.equals(key, ignoreCase = true) }
        }

        @JvmStatic
        fun isEmpty(
            set: EnumSet<SearchResultType>
        ): Boolean = when (set.size) {
            0 -> true
            1 -> set.contains(All)
            values().size - 1 -> !set.contains(All)
            else -> false
        }
    }

    class Adapter : JsonDeserializer<SearchResultType> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): SearchResultType {
            val token = json?.asString.orEmpty()
            return requireNotNull(resolve(token)) { "Failed to resolve type \'$token\'" }
        }
    }
}
