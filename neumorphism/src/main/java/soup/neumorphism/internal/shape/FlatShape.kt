package soup.neumorphism.internal.shape

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.NeumorphShadowDrawable
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withClipOut
import kotlin.math.min

internal class FlatShape(
    private var drawableState: NeumorphShapeDrawableState,
    private val outerShadow: Boolean = true
) : Shape {

    private var shadowBitmap: Bitmap? = null

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    private val shadowPaint = Paint()

    override fun draw(canvas: Canvas, outlinePath: Path) {
        val shadow = shadowBitmap ?: return

        if (outerShadow) canvas.withClipOut(outlinePath) {
            canvas.drawShadow(shadow)
        }

        else canvas.withClip(outlinePath) {
            canvas.drawShadow(shadow)
        }
    }

    private fun Canvas.drawShadow(shadow: Bitmap) {

        drawBitmap(shadow, 0f, 0f, shadowPaint)
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val w = bounds.width()
        val h = bounds.height()

        val shape = when(drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                NeumorphShadowDrawable.Shape.Oval(135f)
            }
            else -> {
                val maxRadius = min(w / 2f, h / 2f)
                val radius = min(
                    maxRadius,
                    drawableState.shapeAppearanceModel.getCornerSize()
                )

                NeumorphShadowDrawable.Shape.Rectangle(radius)
            }
        }

        val theme = if (outerShadow) NeumorphShadowDrawable.Theme(
            drawableState.shadowColorLight,
            drawableState.shadowColorDark
        ) else NeumorphShadowDrawable.Theme(
            drawableState.shadowColorDark,
            drawableState.shadowColorLight
        )

        val style = NeumorphShadowDrawable.Style(
            drawableState.shadowElevation,
            drawableState.blurProvider.defaultBlurRadius
        )

        shadowBitmap = NeumorphShadowDrawable(style, theme, shape).apply {
            alpha = drawableState.alpha
        }.toBlurredBitmap(w, h)
    }

    private fun Drawable.toBlurredBitmap(
        w: Int,
        h: Int
    ): Bitmap? {
        fun Bitmap.blurred(): Bitmap? {
            if (drawableState.inEditMode) {
                return this
            }
            return drawableState.blurProvider.blur(this)
        }

        val shadowOffset = drawableState.shadowElevation.toInt() / 2

        val width = w + (shadowOffset * 2)
        val height = h + (shadowOffset * 2)

        setBounds(
            shadowOffset,
            shadowOffset,
            w + shadowOffset,
            h + shadowOffset
        )

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                draw(this)
            }.blurred()
    }
}