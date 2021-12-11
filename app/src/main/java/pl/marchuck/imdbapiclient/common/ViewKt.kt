package pl.marchuck.imdbapiclient.common

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

fun View.setupCorners(
    @ColorRes colorRes: Int,
    block: ShapeAppearanceModel.Builder.() -> Unit
) {
    val shapeAppearanceModel = ShapeAppearanceModel()
        .toBuilder().apply(block)
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    shapeDrawable.fillColor = ContextCompat.getColorStateList(context, colorRes)
    ViewCompat.setBackground(this, shapeDrawable)
}

fun View.setAllCorners(@ColorRes colorRes: Int, @Dimension cornerSize: Float) =
    setupCorners(
        colorRes = colorRes,
        block = { setAllCorners(CornerFamily.ROUNDED, cornerSize) }
    )