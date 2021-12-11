package pl.marchuck.imdbapiclient.ui.detail

import android.content.res.Resources
import pl.marchuck.imdbapiclient.R
import pl.marchuck.imdbapiclient.imdb.Human

class MovieDetailFormatter(private val resources: Resources) {

    fun formatDuration(durationInMinutes: String): String {
        val minutes = durationInMinutes.toIntOrNull() ?: return ""

        val hours = minutes / 60
        val leftMinutes = minutes % 60

        if (hours > 0) {
            return if (leftMinutes > 0) {
                resources.getString(
                    R.string.imdb__movie_hours_minutes_placeholder,
                    hours,
                    leftMinutes
                )
            } else {
                resources.getString(
                    R.string.imdb__movie_hours_placeholder,
                    hours
                )
            }
        } else {
            return resources.getString(
                R.string.imdb__movie_minutes_placeholder,
                leftMinutes
            )
        }
    }

    fun formatJobInfo(job: String, items: List<Human>): String {
        val label = when {
            items.isEmpty() -> {
                return ""
            }
            items.size > 1 -> {
                job + "s"
            }
            else -> {
                job
            }
        }
        return items.joinToString(prefix = "$label: ") { it.name }
    }
}
