package pl.marchuck.imdbapiclient.imdb

import android.content.Context
import android.os.Build
import java.util.*

class ImdbConfig private constructor(language: String, val apiKey: String) {

    val endpointUrl = "https://imdb-api.com/$language/API/"

    class Factory(private val context: Context) {

        fun create(apiKey: String): ImdbConfig {
            val language = getCurrentLocale().language.lowercase()
            return ImdbConfig(language, apiKey)
        }

        private fun getCurrentLocale(): Locale {
            val configuration = context.resources.configuration
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.locales.get(0)
            } else {
                configuration.locale
            }
        }
    }
}