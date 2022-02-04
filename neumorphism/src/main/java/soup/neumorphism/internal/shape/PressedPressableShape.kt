package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.ShadowDrawable
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

    override fun draw(canvas: Canvas, outlinePath: Path) {
        canvas.withClip(outlinePath) {
            val elevation = drawableState.shadowElevation
            val z = drawableState.shadowElevation + drawableState.translationZ
            val left: Float
            val top: Float
            val inset = drawableState.inset
            left = inset.left.toFloat()
            top = inset.top.toFloat()
            lightShadowBitmap?.let {
                val offset = - elevation + z * 2
                drawBitmap(it, offset, offset, null)
            }
            darkShadowBitmap?.let {
                val offset = elevation - z * 2
                drawBitmap(it, offset, offset, null)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val shadowElevation = drawableState.shadowElevation.toInt()
        val w = bounds.width()
        val h = bounds.height()
        val width: Int = w + shadowElevation
        val height: Int = h + shadowElevation

        val cornerSize = min(
            min(w / 2f, h / 2f),
            drawableState.shapeAppearanceModel.getCornerSize()
        )

        lightShadowBitmap = ShadowDrawable(
            drawableState.shadowElevation,
            cornerSize,
            drawableState.shadowColorLight
        ).apply {
            alpha = drawableState.alpha
            setCoverage(
                ShadowDrawable.Coverage.BOTTOM_RIGHT_CORNER,
                ShadowDrawable.Coverage.BOTTOM_LINE,
                ShadowDrawable.Coverage.RIGHT_LINE,
            )

            setBounds(shadowElevation, shadowElevation, width, height)
        }.toBlurredBitmap(width + shadowElevation, height + shadowElevation)

        darkShadowBitmap = ShadowDrawable(
            drawableState.shadowElevation,
            cornerSize,
            drawableState.shadowColorDark
        ).apply {
            alpha = drawableState.alpha
            setCoverage(
                ShadowDrawable.Coverage.TOP_LEFT_CORNER,
                ShadowDrawable.Coverage.TOP_LINE,
                ShadowDrawable.Coverage.LEFT_LINE,
            )

            setBounds(shadowElevation, shadowElevation, width, height)
        }.toBlurredBitmap(width + shadowElevation, height + shadowElevation)
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