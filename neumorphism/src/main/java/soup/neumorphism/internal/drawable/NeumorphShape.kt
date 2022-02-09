package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.NeumorphShadowDrawable
import soup.neumorphism.internal.shape.Shape
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withClipOut
import kotlin.math.min

internal class NeumorphShape(
    private var drawableState: NeumorphShapeDrawableState,
    private val outerShadow: Boolean = true
) : Shape {

    private var shadowBitmap: NeumorphShadow? = null
    private var bounds = Rect()

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        val shadow = shadowBitmap ?: return

        if (outerShadow) canvas.withClipOut(outlinePath) {
            shadow.draw(canvas, outlinePath)
        }

        else canvas.withClip(outlinePath) {
            shadow.draw(canvas, outlinePath)
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val w = bounds.width()
        val h = bounds.height()

        this.bounds = bounds

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

        shadowBitmap = NeumorphShadow(style, theme, shape)
    }

    private fun NeumorphShadow.toBlurredBitmap(
        w: Int,
        h: Int,
        path: Path
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

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                draw(this, path)
            }.blurred()
    }
}