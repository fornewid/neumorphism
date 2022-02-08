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

internal class PressedPressableShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var shadowBitmap: Bitmap? = null

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    private val shadowPaint = Paint()

    override fun draw(canvas: Canvas, outlinePath: Path) {
        val shadow = shadowBitmap ?: return

        canvas.withClip(outlinePath) {
            canvas.drawShadow(shadow)
        }
    }

    private fun Canvas.drawShadow(shadow: Bitmap) {
        val elevation = drawableState.shadowElevation
        val z = drawableState.shadowElevation + drawableState.translationZ

        val pressPercentage = z / elevation
        shadowPaint.alpha = (255 * pressPercentage).toInt()

        drawBitmap(shadow, 0f, 0f, shadowPaint)
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val shadowElevation = drawableState.shadowElevation.toInt()
        val w = bounds.width()
        val h = bounds.height()
        val width: Int = w + shadowElevation
        val height: Int = h + shadowElevation

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

        val theme = NeumorphShadowDrawable.Theme(
            drawableState.shadowColorLight,
            drawableState.shadowColorDark
        )

        val style = NeumorphShadowDrawable.Style(
            drawableState.shadowElevation,
            drawableState.blurProvider.defaultBlurRadius
        )

        shadowBitmap = NeumorphShadowDrawable(style, theme, shape).apply {
            alpha = drawableState.alpha
            setBounds(shadowElevation, shadowElevation, width, height)
        }.toBlurredBitmap(
            width + shadowElevation,
            height + shadowElevation
        )
    }

    private fun Drawable.toBlurredBitmap(w: Int, h: Int): Bitmap? {
        fun Bitmap.blurred(): Bitmap? {
            if (drawableState.inEditMode) {
                return this
            }
            return drawableState.blurProvider.blur(this)
        }

        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            .onCanvas {
                draw(this)
            }.blurred()
    }
}