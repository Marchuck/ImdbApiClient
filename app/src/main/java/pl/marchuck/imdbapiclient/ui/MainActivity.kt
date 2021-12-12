package pl.marchuck.imdbapiclient.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import pl.marchuck.imdbapiclient.R
import pl.marchuck.imdbapiclient.common.NightModeWrapper
import pl.marchuck.imdbapiclient.ui.list.MovieListFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    val nighModeWrapper by inject<NightModeWrapper> { parametersOf(this) }
    val navigationWrapper by lazy { NavigationWrapper(this, R.id.container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        nighModeWrapper.applyNightOrDayTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
