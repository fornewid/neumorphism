package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.NeumorphShape
import kotlin.math.abs

internal class FlatPressableShape(drawableState: NeumorphShapeDrawableState) : Shape {

    private val flatShape = NeumorphShape(drawableState)
    private val pressedShape = PressedPressableShape(drawableState)

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        flatShape.setDrawableState(newDrawableState)
        val pressedShapeEvaluation = 255 - newDrawableState.alpha
        val pressedShapeState = newDrawableState.copy(alpha = abs(pressedShapeEvaluation))
        pressedShape.setDrawableState(pressedShapeState)
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        flatShape.draw(canvas, outlinePath)
        pressedShape.draw(canvas, outlinePath)
    }

    override fun updateShadowBitmap(bounds: Rect) {
        flatShape.updateShadowBitmap(bounds)
        pressedShape.updateShadowBitmap(bounds)
    }
}
