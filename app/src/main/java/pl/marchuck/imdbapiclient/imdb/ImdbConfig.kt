package pl.marchuck.imdbapiclient.imdb

class ImdbConfig(language: String, val apiKey: String) {
    val endpointUrl = "https://imdb-api.com/$language/API/"

}