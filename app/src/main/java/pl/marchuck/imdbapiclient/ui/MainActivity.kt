package pl.marchuck.imdbapiclient.ui

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import pl.marchuck.imdbapiclient.R
import pl.marchuck.imdbapiclient.common.NightModeWrapper
import pl.marchuck.imdbapiclient.imdb.ImdbClientImpl
import pl.marchuck.imdbapiclient.imdb.ImdbConfig
import pl.marchuck.imdbapiclient.ui.list.MovieListFragment
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {


    val nighModeWrapper by lazy { NightModeWrapper(this) }
    val navigationWrapper by lazy { NavigationWrapper(this, R.id.container) }

    private fun getCurrentLocale(): Locale = with(resources.configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locales.get(0)
        } else {
            locale
        }
    }

    private fun provideLanguageCode(): String = getCurrentLocale().language.lowercase()

    val imdbClient by lazy {
        ImdbClientImpl(
            applicationContext,
            ImdbConfig(
                language = provideLanguageCode(),
                apiKey = "k_vwlwek4j"
//                apiKey = "k_g76m3rxx"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        nighModeWrapper.applyNightOrDayTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())



        if (savedInstanceState == null) {
            navigationWrapper.pushScreen(MovieListFragment.newInstance(), "movie-list")
        }
    }

    override fun onBackPressed() {
        if (!navigationWrapper.pop()) {
            super.onBackPressed()
        }
    }
}
