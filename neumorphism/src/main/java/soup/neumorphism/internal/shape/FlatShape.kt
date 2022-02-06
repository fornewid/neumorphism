package soup.neumorphism.internal.shape

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.ShadowCoverage
import soup.neumorphism.internal.drawable.ShadowCoverage.Rectangle.Sides.*
import soup.neumorphism.internal.drawable.ShadowDrawable
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClipOut
import kotlin.math.min


internal class FlatShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var lightShadowBitmap: Bitmap? = null
    private var darkShadowBitmap: Bitmap? = null

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

            darkShadowBitmap?.let {
                drawBitmap(it, elevation/4, elevation/4, shadowPaint)
            }

            lightShadowBitmap?.let {
                drawBitmap(it, -elevation/4, -elevation/4, shadowPaint)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val w = bounds.width()
        val h = bounds.height()

        val minRadius = min(w / 2f, h / 2f)
        val cornerSize = when(drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                minRadius
            }
            else -> min(
                minRadius,
                drawableState.shapeAppearanceModel.getCornerSize()
            )
        }

        val lightShadowCoverage = ShadowCoverage.Rectangle(cornerSize).apply {
            setCoverage(
                TOP_LEFT_CORNER,
                BOTTOM_LEFT_CORNER,
                TOP_RIGHT_CORNER,
                TOP_LINE,
                LEFT_LINE
            )
        }

        lightShadowBitmap = ShadowDrawable(
            drawableState.shadowElevation,
            drawableState.shadowColorLight,
            lightShadowCoverage
        ).apply {
            alpha = drawableState.alpha
        }.toBlurredBitmap(w, h)

        val darkShadowCoverage = ShadowCoverage.Rectangle(cornerSize).apply {
            setCoverage(
                BOTTOM_RIGHT_CORNER,
                BOTTOM_LEFT_CORNER,
                TOP_RIGHT_CORNER,
                RIGHT_LINE,
                BOTTOM_LINE
            )
        }

        darkShadowBitmap = ShadowDrawable(
            drawableState.shadowElevation,
            drawableState.shadowColorDark,
            darkShadowCoverage
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