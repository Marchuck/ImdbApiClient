package pl.marchuck.imdbapiclient.common

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import pl.marchuck.imdbapiclient.R

class NightModeWrapper(private val activity: Activity) {

    companion object {
        private const val DARK_MODE_ON = "DARK_MODE_ON"
    }

    private fun sharedPrefs(): SharedPreferences {
        return activity.getSharedPreferences(
            activity.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }

    private var darkModeInUse: Boolean = false
        set(value) {
            field = value
            val prefs = sharedPrefs()
            prefs.edit().putBoolean(DARK_MODE_ON, value).commit()
            activity.recreate()
        }
        get() {
            val prefs = sharedPrefs()
            return prefs.getBoolean(DARK_MODE_ON, false)
        }

    fun toggleDarkMode() {
        darkModeInUse = !darkModeInUse
        activity.recreate()
    }

    fun applyNightOrDayTheme() {
        val mode = if (darkModeInUse) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    @DrawableRes
    fun darkModeIcon(): Int {
        return if (darkModeInUse) {
            R.drawable.ic_night
        } else {
            R.drawable.ic_sun
        }
    }
}
