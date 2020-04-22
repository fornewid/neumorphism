package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClipOut
import soup.neumorphism.internal.util.withTranslation
import kotlin.math.roundToInt

internal class FlatShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var shadowBitmap: Bitmap? = null
    private val lightShadowDrawable = GradientDrawable()
    private val darkShadowDrawable = GradientDrawable()

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        canvas.withClipOut(outlinePath) {
            shadowBitmap?.let {
                val offset = (drawableState.shadowElevation * 2).unaryMinus()
                canvas.drawBitmap(it, offset, offset, null)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        lightShadowDrawable.apply {
            setColor(drawableState.shadowColorLight)
            when (drawableState.shapeAppearanceModel.getCornerFamily()) {
                CornerFamily.OVAL -> {
                    shape = GradientDrawable.OVAL
                }
                CornerFamily.ROUNDED -> {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = drawableState.shapeAppearanceModel.getCornerSize().let {
                        floatArrayOf(it, it, it, it, it, it, it, it)
                    }
                }
            }
        }
        darkShadowDrawable.apply {
            setColor(drawableState.shadowColorDark)

            when (drawableState.shapeAppearanceModel.getCornerFamily()) {
                CornerFamily.OVAL -> {
                    shape = GradientDrawable.OVAL
                }
                CornerFamily.ROUNDED -> {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = drawableState.shapeAppearanceModel.getCornerSize().let {
                        floatArrayOf(it, it, it, it, it, it, it, it)
                    }
                }
            }
        }

        val w = bounds.width()
        val h = bounds.height()
        lightShadowDrawable.setSize(w, h)
        lightShadowDrawable.setBounds(0, 0, w, h)
        darkShadowDrawable.setSize(w, h)
        darkShadowDrawable.setBounds(0, 0, w, h)
        shadowBitmap = generateShadowBitmap(w, h)
    }

    private fun generateShadowBitmap(w: Int, h: Int): Bitmap? {
        fun Bitmap.blurred(): Bitmap? {
            return drawableState.blurProvider.blur(this)
        }

        val shadowElevation = drawableState.shadowElevation
        val width = (w + shadowElevation * 4).roundToInt()
        val height = (h + shadowElevation * 4).roundToInt()
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                withTranslation(shadowElevation, shadowElevation) {
                    lightShadowDrawable.draw(this)
                }
                withTranslation(shadowElevation * 3, shadowElevation * 3) {
                    darkShadowDrawable.draw(this)
                }
            }
            .blurred()
    }
}