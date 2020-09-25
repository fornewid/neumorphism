package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.LightSource
import soup.neumorphism.NeumorphShapeAppearanceModel
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClipOut
import soup.neumorphism.internal.util.withTranslation
import kotlin.math.min
import kotlin.math.roundToInt

internal class FlatShape(
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
        canvas.withClipOut(outlinePath) {
            val lightSource = drawableState.lightSource
            val elevation = drawableState.shadowElevation
            val z = drawableState.shadowElevation + drawableState.translationZ
            val inset = drawableState.inset
            val left = inset.left.toFloat()
            val top = inset.top.toFloat()
            lightShadowBitmap?.let {
                val offsetX = if (LightSource.isLeft(lightSource)) -elevation - z else -elevation + z
                val offsetY = if (LightSource.isTop(lightSource)) -elevation - z else -elevation + z
                drawBitmap(it, offsetX + left, offsetY + top, null)
            }
            darkShadowBitmap?.let {
                val offsetX = if (LightSource.isLeft(lightSource)) -elevation + z else -elevation - z
                val offsetY = if (LightSource.isTop(lightSource)) -elevation + z else -elevation - z
                drawBitmap(it, offsetX + left, offsetY + top, null)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        fun GradientDrawable.setCornerShape(shapeAppearanceModel: NeumorphShapeAppearanceModel) {
            when (shapeAppearanceModel.getCornerFamily()) {
                CornerFamily.OVAL -> {
                    shape = GradientDrawable.OVAL
                }
                CornerFamily.ROUNDED -> {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = shapeAppearanceModel.getCornerRadii(
                        min(bounds.width() / 2f, bounds.height() / 2f)
                    )
                }
            }
        }

        lightShadowDrawable.apply {
            setColor(drawableState.shadowColorLight)
            setCornerShape(drawableState.shapeAppearanceModel)
        }
        darkShadowDrawable.apply {
            setColor(drawableState.shadowColorDark)
            setCornerShape(drawableState.shapeAppearanceModel)
        }

        val w = bounds.width()
        val h = bounds.height()
        lightShadowDrawable.setSize(w, h)
        lightShadowDrawable.setBounds(0, 0, w, h)
        darkShadowDrawable.setSize(w, h)
        darkShadowDrawable.setBounds(0, 0, w, h)
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