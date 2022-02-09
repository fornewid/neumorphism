package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.NeumorphShadow
import soup.neumorphism.internal.drawable.NeumorphShape

internal class BasinShape(drawableState: NeumorphShapeDrawableState) : Shape {

    private val shadows by lazy {
        val theme = NeumorphShadow.Theme(
            drawableState.shadowColorLight,
            drawableState.shadowColorDark
        )

        val style = NeumorphShadow.Style(
            drawableState.shadowElevation,
            drawableState.blurRadius,
            drawableState.shapeAppearanceModel.getCornerFamily(),
            drawableState.shapeAppearanceModel.getCornerSize()
        )

        listOf(
            NeumorphShape(style, theme, isOuterShadow = true),
            NeumorphShape(style, theme, isOuterShadow = false)
        )
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        shadows.forEach { it.draw(canvas, outlinePath) }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        shadows.forEach { it.updateShadowBitmap(bounds) }
    }
}
