package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.drawable.ShadowCoverage
import soup.neumorphism.internal.drawable.ShadowCoverage.Rectangle.Sides.*
import soup.neumorphism.internal.drawable.ShadowDrawable
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import kotlin.math.min

internal class PressedShape(
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

            darkShadowBitmap?.let {
                drawBitmap(it, -elevation/4, -elevation/4, null)
            }

            lightShadowBitmap?.let {
                drawBitmap(it, elevation/4, elevation/4, null)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val w = bounds.width()
        val h = bounds.height()

        val minRadius = min(w / 2f, h / 2f)
        val cornerSize = when(drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> minRadius
            else -> min(
                minRadius,
                drawableState.shapeAppearanceModel.getCornerSize()
            )
        }

        val lightShadowCoverage = ShadowCoverage.Rectangle(cornerSize).apply {
            setCoverage(
                BOTTOM_RIGHT_CORNER,
                BOTTOM_LINE,
                RIGHT_LINE
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
                TOP_LEFT_CORNER,
                TOP_LINE,
                LEFT_LINE
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

        val shadowOffset = drawableState.shadowElevation.toInt() / 2
        setBounds(
            shadowOffset,
            shadowOffset,
            w + shadowOffset,
            h + shadowOffset
        )

        val width = w + shadowOffset * 2
        val height = h + shadowOffset * 2
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                draw(this)
            }.blurred()
    }
}