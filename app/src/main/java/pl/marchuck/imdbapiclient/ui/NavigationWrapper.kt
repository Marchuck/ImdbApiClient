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

    fun pop(): Boolean = if (fragmentManager.fragments.size > 1) {
        fragmentManager.popBackStackImmediate()
        fragmentManager.fragments.size > 1
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
