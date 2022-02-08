package soup.neumorphism.internal.shape

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.ShadowCoverage.Rectangle
import soup.neumorphism.internal.drawable.NeumorphShadowDrawable
import soup.neumorphism.internal.drawable.ShadowCoverage
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withTranslation
import kotlin.math.min
import kotlin.math.roundToInt

internal class PressedPressableShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var shadowBitmap: Bitmap? = null

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    private val shadowPaint = Paint()

    override fun draw(canvas: Canvas, outlinePath: Path) {
        canvas.withClip(outlinePath) {
            val elevation = drawableState.shadowElevation
            val z = drawableState.shadowElevation + drawableState.translationZ

            val pressPercentage = 1 - (z / elevation)
            shadowPaint.alpha = (255 * pressPercentage).toInt()

            shadowBitmap?.let {
                drawBitmap(it, 0f, 0f, shadowPaint)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val shadowElevation = drawableState.shadowElevation.toInt()
        val w = bounds.width()
        val h = bounds.height()
        val width: Int = w + shadowElevation
        val height: Int = h + shadowElevation

        val shadowCoverage = when(drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                ShadowCoverage.Oval(135f)
            }
            else -> {
                val maxRadius = min(w / 2f, h / 2f)
                val radius = min(
                    maxRadius,
                    drawableState.shapeAppearanceModel.getCornerSize()
                )

                Rectangle(radius)
            }
        }

        shadowBitmap = NeumorphShadowDrawable(
            drawableState.shadowElevation,
            drawableState.shadowColorLight,
            drawableState.shadowColorDark,
            shadowCoverage
        ).apply {
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