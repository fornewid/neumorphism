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
            bounds.left + shadowOffset,
            bounds.top + shadowOffset,
            bounds.right + shadowOffset,
            bounds.bottom + shadowOffset
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
       drawRectangleShadow(Shape.Rectangle(shadowBounds.width() / 2))
    }

    private fun Canvas.drawRectangleShadow(coverage: Shape.Rectangle) {
        val radius = coverage.radius
        val forthRadius = radius / 4
        val startX = shadowBounds.left + radius
        val endX = shadowBounds.right - radius
        val startY = shadowBounds.top + radius
        val endY = shadowBounds.bottom - radius

        //Light shadow drawing
        paint.color = theme.lightColor

        withTranslation(
            -shadowOffset,
            -shadowOffset
        ) {
            drawLine(shadowBounds.left, startY, shadowBounds.left, endY, paint)
            drawCurvedArc(shadowBounds.left, startY, startX, shadowBounds.top, radius - forthRadius, paint)
            drawLine(startX, shadowBounds.top, endX, shadowBounds.top, paint)

            drawCurvedArc(
                shadowBounds.left + forthRadius,
                shadowBounds.bottom - forthRadius,
                shadowBounds.left,
                endY,
                0f,
                paint
            )

            drawCurvedArc(
                endX,
                shadowBounds.top,
                shadowBounds.right - forthRadius,
                shadowBounds.top + forthRadius,
                0f,
                paint
            )
        }

        //Dark shadow
        paint.color = theme.darkColor

        withTranslation(
            -shadowOffset,
            -shadowOffset
        ) {
            drawCurvedArc(shadowBounds.right, endY, endX, shadowBounds.bottom, radius, paint)
            drawLine(startX, shadowBounds.bottom, endX, shadowBounds.bottom, paint)
            drawLine(shadowBounds.right, startY, shadowBounds.right, endY, paint)

            drawCurvedArc(
                shadowBounds.right - forthRadius,
                shadowBounds.top + forthRadius,
                shadowBounds.right,
                startY,
                0f,
                paint
            )

            drawCurvedArc(
                startX,
                shadowBounds.bottom,
                shadowBounds.left + forthRadius,
                shadowBounds.bottom - forthRadius,
                0f,
                paint
            )
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