package soup.neumorphism.internal.drawable

import android.graphics.*
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.shape.Shape
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withClipOut
import kotlin.math.min

internal class NeumorphShape(
    private var drawableState: NeumorphShapeDrawableState,
    private val outerShadow: Boolean = true
) : Shape {

    private var shadowBitmap: Bitmap? = null
    private var shadow: NeumorphShadow? = null

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    private val shadowPaint = Paint()

    override fun draw(canvas: Canvas, outlinePath: Path) {
        val bitmap = shadowBitmap
        if (bitmap != null) {
            if (outerShadow) canvas.withClipOut(outlinePath) {
                canvas.drawBitmap(bitmap, 0f, 0f, shadowPaint)
            } else canvas.withClip(outlinePath) {
                canvas.drawBitmap(bitmap, 0f, 0f, shadowPaint)
            }

            return
        }

        val shadow = shadow
        if (shadow != null) {
            shadowBitmap = shadow.drawToBitmap()
            return draw(canvas, outlinePath)
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val theme = NeumorphShadowDrawable.Theme(
            drawableState.shadowColorLight,
            drawableState.shadowColorDark
        )

        val style = NeumorphShadowDrawable.Style(
            drawableState.shadowElevation,
            drawableState.blurProvider.defaultBlurRadius
        )

        shadow = if (outerShadow) NeumorphOuterShadow(drawableState, style, theme, bounds)
        else NeumorphInnerShadow(drawableState, style, theme, bounds)
    }
}