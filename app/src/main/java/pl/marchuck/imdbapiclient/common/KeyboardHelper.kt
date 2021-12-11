package pl.marchuck.imdbapiclient.common

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}