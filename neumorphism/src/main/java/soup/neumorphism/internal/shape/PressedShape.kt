package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.LightSource
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
                        getCornerSizeForLightShadow()
                    )
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = getCornerRadiiForLightShadow(cornerSize)
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
                        getCornerSizeForDarkShadow()
                    )
                    shape = GradientDrawable.RECTANGLE
                    cornerRadii = getCornerRadiiForDarkShadow(cornerSize)
                }
            }
        }

        lightShadowDrawable.setSize(width, height)
        lightShadowDrawable.setBounds(0, 0, width, height)
        darkShadowDrawable.setSize(width, height)
        darkShadowDrawable.setBounds(0, 0, width, height)
        shadowBitmap = generateShadowBitmap(w, h)
    }

    private fun getCornerSizeForLightShadow(): Float {
        return drawableState.shapeAppearanceModel.run {
            when (drawableState.lightSource) {
                LightSource.LEFT_TOP -> getBottomLeftCornerSize()
                LightSource.LEFT_BOTTOM -> getTopRightCornerSize()
                LightSource.RIGHT_TOP -> getBottomRightCornerSize()
                LightSource.RIGHT_BOTTOM -> getTopLeftCornerSize()
                else -> throw IllegalStateException("LightSource ${drawableState.lightSource} is not supported.")
            }
        }
    }

    private fun getCornerRadiiForLightShadow(cornerSize: Float): FloatArray {
        return when (drawableState.lightSource) {
            LightSource.LEFT_TOP -> floatArrayOf(0f, 0f, 0f, 0f, cornerSize, cornerSize, 0f, 0f)
            LightSource.LEFT_BOTTOM -> floatArrayOf(0f, 0f, cornerSize, cornerSize, 0f, 0f, 0f, 0f)
            LightSource.RIGHT_TOP -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, cornerSize, cornerSize)
            LightSource.RIGHT_BOTTOM -> floatArrayOf(cornerSize, cornerSize, 0f, 0f, 0f, 0f, 0f, 0f)
            else -> throw IllegalStateException("LightSource ${drawableState.lightSource} is not supported.")
        }
    }

    private fun getCornerSizeForDarkShadow(): Float {
        return drawableState.shapeAppearanceModel.run {
            when (drawableState.lightSource) {
                LightSource.LEFT_TOP -> getTopLeftCornerSize()
                LightSource.LEFT_BOTTOM -> getBottomLeftCornerSize()
                LightSource.RIGHT_TOP -> getTopRightCornerSize()
                LightSource.RIGHT_BOTTOM -> getBottomRightCornerSize()
                else -> throw IllegalStateException("LightSource ${drawableState.lightSource} is not supported.")
            }
        }
    }

    private fun getCornerRadiiForDarkShadow(cornerSize: Float): FloatArray {
        return when (drawableState.lightSource) {
            LightSource.LEFT_TOP -> floatArrayOf(cornerSize, cornerSize, 0f, 0f, 0f, 0f, 0f, 0f)
            LightSource.LEFT_BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, cornerSize, cornerSize)
            LightSource.RIGHT_TOP -> floatArrayOf(0f, 0f, cornerSize, cornerSize, 0f, 0f, 0f, 0f)
            LightSource.RIGHT_BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, cornerSize, cornerSize, 0f, 0f)
            else -> throw IllegalStateException("LightSource ${drawableState.lightSource} is not supported.")
        }
    }

    private fun generateShadowBitmap(w: Int, h: Int): Bitmap? {
        fun Bitmap.blurred(): Bitmap? {
            if (drawableState.inEditMode) {
                return this
            }
            return drawableState.blurProvider.blur(this)
        }

        val shadowElevation = drawableState.shadowElevation
        val lightSource = drawableState.lightSource
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            .onCanvas {
                withTranslation(
                    x = if (LightSource.isLeft(lightSource)) -shadowElevation else 0f,
                    y = if (LightSource.isTop(lightSource)) -shadowElevation else 0f
                ) {
                    lightShadowDrawable.draw(this)
                }
                withTranslation(
                    x = if (LightSource.isRight(lightSource)) -shadowElevation else 0f,
                    y = if (LightSource.isBottom(lightSource)) -shadowElevation else 0f
                ) {
                    darkShadowDrawable.draw(this)
                }
            }
            .blurred()
    }
}