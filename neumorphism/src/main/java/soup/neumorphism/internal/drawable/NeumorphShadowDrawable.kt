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

        val topRightStartArcBound = RectF(
            shadowBounds.right - radiusOffset - radius/2,
            shadowBounds.top,
            shadowBounds.right - radius/2,
            shadowBounds.top + radiusOffset
        )

        val topRightEndArcBound = RectF(
            shadowBounds.right - radiusOffset,
            shadowBounds.top + radius/2,
            shadowBounds.right,
            shadowBounds.top + radiusOffset + radius/2
        )

        val bottomLeftStartArcBound = RectF(
            shadowBounds.left,
            shadowBounds.bottom - radiusOffset - radius/2,
            shadowBounds.left + radiusOffset,
            shadowBounds.bottom - radius/2
        )

        val bottomLeftEndArcBound = RectF(
            shadowBounds.left + radius/2,
            shadowBounds.bottom - radiusOffset,
            shadowBounds.left + radiusOffset + radius/2,
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
            drawLine(startX, shadowBounds.top, endX - radius/2, shadowBounds.top, paint)
            drawArc(topRightStartArcBound, -90f, 90f, false, paint)
            drawLine(shadowBounds.left, startY, shadowBounds.left, endY - radius/2, paint)
            drawArc(topLeftArcBound, 180f, 90f, false, paint)
            drawArc(bottomLeftStartArcBound, 90f, 90f, false, paint)
        }

        withTranslation(
            shadowOffset,
            shadowOffset
        ) {
            //Dark shadow
            paint.color = theme.darkColor
            drawArc(topRightEndArcBound, -90f, 90f, false, paint)
            drawArc(bottomRightArcBound, 0f, 90f, false, paint)
            drawLine(startX + radius/2, shadowBounds.bottom, endX, shadowBounds.bottom, paint)
            drawLine(shadowBounds.right, startY + radius/2, shadowBounds.right, endY, paint)
            drawArc(bottomLeftEndArcBound, 90f, 90f, false, paint)
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