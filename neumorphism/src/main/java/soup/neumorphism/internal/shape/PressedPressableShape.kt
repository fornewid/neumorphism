package soup.neumorphism.internal.shape

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.ShadowDrawable
import soup.neumorphism.internal.drawable.ShadowDrawable.Coverage.*
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withTranslation
import kotlin.math.min
import kotlin.math.roundToInt

internal class PressedPressableShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var lightShadowBitmap: Bitmap? = null
    private var darkShadowBitmap: Bitmap? = null

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

            lightShadowBitmap?.let {
                drawBitmap(it, 0f, 0f, shadowPaint)
            }

            darkShadowBitmap?.let {
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

        val minRadius = min(w / 2f, h / 2f)
        val cornerSize = when(drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> minRadius
            else -> min(
                minRadius,
                drawableState.shapeAppearanceModel.getCornerSize()
            )
        }

        lightShadowBitmap = ShadowDrawable(
            drawableState.shadowElevation,
            cornerSize,
            drawableState.shadowColorLight
        ).apply {
            alpha = drawableState.alpha
            setBounds(shadowElevation, shadowElevation, width, height)
            setCoverage(BOTTOM_RIGHT_CORNER, BOTTOM_LINE, RIGHT_LINE)
        }.toBlurredBitmap(
            width + shadowElevation,
            height + shadowElevation
        )

        darkShadowBitmap = ShadowDrawable(
            drawableState.shadowElevation,
            cornerSize,
            drawableState.shadowColorDark
        ).apply {
            alpha = drawableState.alpha
            setBounds(shadowElevation, shadowElevation, width, height)
            setCoverage(TOP_LEFT_CORNER, TOP_LINE, LEFT_LINE)
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

        val shadowElevation = drawableState.shadowElevation
        val width = (w + shadowElevation * 2).roundToInt()
        val height = (h + shadowElevation * 2).roundToInt()
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                withTranslation(shadowElevation, shadowElevation) {
                    draw(this)
                }
            }
            .blurred()
    }
}