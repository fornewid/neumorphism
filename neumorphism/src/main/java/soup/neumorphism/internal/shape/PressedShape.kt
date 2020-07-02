package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withTranslation
import kotlin.math.min

internal class PressedShape(
    private var drawableState: NeumorphShapeDrawableState
) : Shape {

    private var shadowBitmap: Bitmap? = null
    private val lightShadowDrawable = GradientDrawable()
    private val darkShadowDrawable = GradientDrawable()

    override fun setDrawableState(newDrawableState: NeumorphShapeDrawableState) {
        this.drawableState = newDrawableState
    }

    override fun draw(canvas: Canvas, outlinePath: Path) {
        canvas.withClip(outlinePath) {
            shadowBitmap?.let {
                val left: Float
                val top: Float
                val inset = drawableState.inset
                left = inset.left.toFloat()
                top = inset.top.toFloat()
                drawBitmap(it, left, top, null)
            }
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        val shadowElevation = drawableState.shadowElevation.toInt()
        val w = bounds.width()
        val h = bounds.height()
        val width: Int = w + shadowElevation
        val height: Int = h + shadowElevation

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

        lightShadowDrawable.setSize(width, height)
        lightShadowDrawable.setBounds(0, 0, width, height)
        darkShadowDrawable.setSize(width, height)
        darkShadowDrawable.setBounds(0, 0, width, height)
        shadowBitmap = generateShadowBitmap(w, h)
    }

    private fun generateShadowBitmap(w: Int, h: Int): Bitmap? {
        fun Bitmap.blurred(): Bitmap? {
            if (drawableState.inEditMode) {
                return this
            }
            return drawableState.blurProvider.blur(this)
        }

        val shadowElevation = drawableState.shadowElevation
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            .onCanvas {
                withTranslation(-shadowElevation, -shadowElevation) {
                    lightShadowDrawable.draw(this)
                }
                darkShadowDrawable.draw(this)
            }
            .blurred()
    }
}