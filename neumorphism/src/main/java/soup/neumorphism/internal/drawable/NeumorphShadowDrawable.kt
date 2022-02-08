package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.internal.util.CanvasCompat.drawCurvedArc
import soup.neumorphism.internal.util.withTranslation

class NeumorphShadowDrawable(
    private val style: Style,
    private val theme: Theme,
    private val shape: Shape
) : Drawable() {

    private val shadowOffset = style.elevation / 2

    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = shadowOffset
        }
    }

    private val shadowBounds: RectF by lazy {
        RectF(
            bounds.left - shadowOffset,
            bounds.top - shadowOffset,
            bounds.right - shadowOffset,
            bounds.bottom - shadowOffset
        )
    }

    override fun draw(canvas: Canvas) {
        when(shape) {
            is Shape.Oval -> canvas.drawOvalShadow(shape)
            is Shape.Rectangle -> canvas.drawRectangleShadow(shape)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }
    private fun Canvas.drawOvalShadow(coverage: Shape.Oval) {
        drawRectangleShadow(Shape.Rectangle(
            shadowBounds.width() / 2
        ))
    }

    private fun Canvas.drawRectangleShadow(coverage: Shape.Rectangle) {
        val radius = coverage.radius
        val radiusOffset = radius * 2

        val topLeftArcBound = RectF(
            shadowBounds.left,
            shadowBounds.top,
            shadowBounds.left + radiusOffset,
            shadowBounds.top + radiusOffset
        )

        val topRightArcBound = RectF(
            shadowBounds.right - radiusOffset,
            shadowBounds.top,
            shadowBounds.right,
            shadowBounds.top + radiusOffset
        )

        val bottomLeftArcBound = RectF(
            shadowBounds.left,
            shadowBounds.bottom - radiusOffset,
            shadowBounds.left + radiusOffset,
            shadowBounds.bottom
        )

        val bottomRightArcBound = RectF(
            shadowBounds.right - radiusOffset,
            shadowBounds.bottom - radiusOffset,
            shadowBounds.right,
            shadowBounds.bottom
        )

        val startX = shadowBounds.left + radius
        val endX = shadowBounds.right - radius
        val startY = shadowBounds.top + radius
        val endY = shadowBounds.bottom - radius

        withTranslation(
            shadowOffset,
            shadowOffset
        ) {
            //Light shadow drawing
            paint.color = theme.lightColor
            drawLine(startX, shadowBounds.top, endX, shadowBounds.top, paint)
            drawArc(topRightArcBound, -90f, 45f, false, paint)
            drawLine(shadowBounds.left, startY, shadowBounds.left, endY, paint)
            drawArc(topLeftArcBound, 180f, 90f, false, paint)
            drawArc(bottomLeftArcBound, 135f, 45f, false, paint)
        }

        withTranslation(
            shadowOffset,
            shadowOffset
        ) {
            //Dark shadow
            paint.color = theme.darkColor
            drawArc(topRightArcBound, -45f, 45f, false, paint)
            drawArc(bottomRightArcBound, 0f, 90f, false, paint)
            drawLine(startX, shadowBounds.bottom, endX, shadowBounds.bottom, paint)
            drawArc(bottomLeftArcBound, 90f, 45f, false, paint)
            drawLine(shadowBounds.right, startY, shadowBounds.right, endY, paint)
        }
    }

    sealed class Shape {

        class Oval(
            val startAngle: Float
        ) : Shape()

        class Rectangle(
            val radius: Float
        ) : Shape()
    }

    data class Theme(
        val lightColor: Int,
        val darkColor: Int
    )

    data class Style(
        val elevation: Float,
        val margin: Float
    )
}