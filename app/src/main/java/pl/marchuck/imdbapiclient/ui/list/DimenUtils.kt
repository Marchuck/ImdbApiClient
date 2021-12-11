package pl.marchuck.imdbapiclient.ui.list

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue


fun Resources.dpToPx(value: Float): Float {
    return displayMetrics.dpToPx(value)
}

fun DisplayMetrics.dpToPx(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this)
}