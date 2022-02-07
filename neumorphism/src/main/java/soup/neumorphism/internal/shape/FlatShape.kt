package soup.neumorphism.internal.shape

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.ShadowCoverage
import soup.neumorphism.internal.drawable.NeumorphShadowDrawable
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClipOut
import kotlin.math.min


internal class FlatShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var shadowBitmap: Bitmap? = null

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    private val shadowPaint = Paint()

    override fun draw(canvas: Canvas, outlinePath: Path) {
        canvas.withClipOut(outlinePath) {
            val elevation = drawableState.shadowElevation
            val z = drawableState.shadowElevation + drawableState.translationZ

            val pressPercentage = z / elevation
            shadowPaint.alpha = (255 * pressPercentage).toInt()

            shadowBitmap?.let {
                drawBitmap(it, -elevation/4, -elevation/4, shadowPaint)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val w = bounds.width()
        val h = bounds.height()

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

                ShadowCoverage.Rectangle(radius)
            }
        }

        shadowBitmap = NeumorphShadowDrawable(
            drawableState.shadowElevation,
            drawableState.shadowColorLight,
            drawableState.shadowColorDark,
            shadowCoverage
        ).apply {
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

        val shadowOffset = drawableState.shadowElevation.toInt()

        setBounds(
            shadowOffset,
            shadowOffset,
            w,
            h
        )

        val width = w + shadowOffset
        val height = h + shadowOffset
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                draw(this)
            }.blurred()
    }
}