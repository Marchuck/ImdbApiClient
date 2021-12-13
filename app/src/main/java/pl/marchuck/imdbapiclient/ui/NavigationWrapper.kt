package pl.marchuck.imdbapiclient.ui

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class NavigationWrapper constructor(
    private val activity: FragmentActivity,
    @IdRes
    private val resId: Int
) {
    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    private val size: Int
        get() = fragmentManager.fragments.size

    fun pop(): Boolean = if (size > 0) {
        fragmentManager.popBackStackImmediate()
        size > 0
    } else {
        false
    }

    fun pushScreen(screen: Fragment, tag: String) {
        fragmentManager.beginTransaction()
            .replace(resId, screen)
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .addToBackStack(tag)
            .commit()
    }
}
