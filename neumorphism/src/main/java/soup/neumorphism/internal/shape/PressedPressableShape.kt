package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.util.BitmapUtils.toBitmap
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
    private val lightShadowDrawable = GradientDrawable()
    private val darkShadowDrawable = GradientDrawable()

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
                val offset = elevation - z
                drawBitmap(it, -offset, -offset, null)
            }
            darkShadowBitmap?.let {
                val offset = -elevation + z
                drawBitmap(it, - offset, - offset, null)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val shadowElevation = drawableState.shadowElevation.toInt()
        val w = bounds.width()
        val h = bounds.height()
        val width: Int = w + shadowElevation
        val height: Int = h + shadowElevation
        val actualWidth = width * 2
        val actualHeight = height * 2

        lightShadowDrawable.apply {
            setSize(width, height)
            setStroke(shadowElevation, drawableState.shadowColorLight)

            when (drawableState.shapeAppearanceModel.getCornerFamily()) {
                CornerFamily.OVAL -> {
                    shape = GradientDrawable.OVAL
                }
                CornerFamily.ROUNDED -> {
                    val cornerSize = min(
                        min(w / 2f, h / 2f),
                        drawableState.shapeAppearanceModel.getCornerSize()
                    )
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, cornerSize, cornerSize, 0f, 0f)
                }
            }
        }

        val lightBitmap = lightShadowDrawable.toBitmap(w, h)
        lightBitmap.height
        darkShadowDrawable.apply {
            setSize(width, height)
            setStroke(shadowElevation, drawableState.shadowColorDark)

            when (drawableState.shapeAppearanceModel.getCornerFamily()) {
                CornerFamily.OVAL -> {
                    shape = GradientDrawable.OVAL
                }
                CornerFamily.ROUNDED -> {
                    val cornerSize = min(
                        min(w / 2f, h / 2f),
                        drawableState.shapeAppearanceModel.getCornerSize()
                    )
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = floatArrayOf(cornerSize, cornerSize, 0f, 0f, 0f, 0f, 0f, 0f)
                }
            }
        }

        lightShadowDrawable.setSize(actualWidth, actualHeight)
        lightShadowDrawable.setBounds(w - shadowElevation, h - shadowElevation, actualWidth, actualHeight)
        darkShadowDrawable.setSize(actualWidth, actualHeight)
        darkShadowDrawable.setBounds(0, 0, actualWidth, actualHeight)
        lightShadowBitmap = lightShadowDrawable.toBlurredBitmap(w, h)
        darkShadowBitmap = darkShadowDrawable.toBlurredBitmap(w, h)
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