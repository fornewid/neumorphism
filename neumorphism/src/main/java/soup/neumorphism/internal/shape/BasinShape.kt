package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.NeumorphShape

internal class BasinShape(drawableState: NeumorphShapeDrawableState) : Shape {

    private val shadows = listOf(
        NeumorphShape(drawableState),
        NeumorphShape(drawableState, outerShadow = false)
    )

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        shadows.forEach { it.setDrawableState(newDrawableState) }
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        shadows.forEach { it.draw(canvas, outlinePath) }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        shadows.forEach { it.updateShadowBitmap(bounds) }
    }
}
