package pl.marchuck.imdbapiclient.ui

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import timber.log.Timber

class NavigationWrapper constructor(
    private val activity: FragmentActivity,
    @IdRes
    private val resId: Int
) {

    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    fun pop(): Boolean {
        return if (hasFragments()) {
            fragmentManager.popBackStack()
            true
        } else {
            false
        }
    }

    private fun hasFragments(): Boolean {
        return fragmentManager.fragments.size > 1
    }

    fun pushScreen(screen: Fragment, tag: String? = null) {
        fragmentManager.beginTransaction()
            .replace(resId, screen, tag)
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .addToBackStack(tag)
            .commit()
    }
}
