package soup.neumorphism.internal.drawable

import android.graphics.*
import soup.neumorphism.internal.util.withClip

class NeumorphInnerShadow(
    appearance: Style,
    theme: Theme,
    bounds: Rect
) : NeumorphShadow(appearance, theme, bounds) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = appearance.elevation.toFloat()
        maskFilter = BlurMaskFilter(
            appearance.radius.toFloat(),
            BlurMaskFilter.Blur.NORMAL
        )
    }

    override fun draw(canvas: Canvas) = with(canvas) {
        val radius = cornerRadius
        val halfRadius = radius / 2
        val doubleRadius = radius * 2

        val shadowBounds = RectF(bounds)
        val startX = shadowBounds.left + radius
        val endX = shadowBounds.right - radius
        val startY = shadowBounds.top + radius
        val endY = shadowBounds.bottom - radius

        val lightPath = Path().apply {
            //Top line
            moveTo(startX, shadowBounds.top)
            lineTo(endX, shadowBounds.top)

            //Left line
            moveTo(shadowBounds.left, startY)
            lineTo(shadowBounds.left, endY)

            //Top right corner (Half)
            addArc(
                shadowBounds.right - doubleRadius,
                shadowBounds.top,
                shadowBounds.right,
                shadowBounds.top + halfRadius,
                -90f,
                45f
            )

            //Top left corner
            addArc(
                shadowBounds.left,
                shadowBounds.top,
                shadowBounds.left + doubleRadius,
                shadowBounds.top + doubleRadius,
                180f,
                90f
            )

            //Bottom left corner (Half)
            addArc(
                shadowBounds.left,
                shadowBounds.bottom - doubleRadius,
                shadowBounds.left + halfRadius,
                shadowBounds.bottom,
                135f,
                45f
            )
        }

        val darkPath = Path().apply {
            //Bottom line
            moveTo(startX, shadowBounds.bottom)
            lineTo(endX, shadowBounds.bottom)

            //Right line
            moveTo(shadowBounds.right, startY)
            lineTo(shadowBounds.right, endY)

            //Top right corner (Half)
            addArc(
                shadowBounds.right - halfRadius,
                shadowBounds.top,
                shadowBounds.right,
                shadowBounds.top + doubleRadius,
                -45f,
                45f
            )

            //Bottom right corner
            addArc(
                shadowBounds.right - doubleRadius,
                shadowBounds.bottom - doubleRadius,
                shadowBounds.right,
                shadowBounds.bottom,
                0f,
                90f
            )

            //Bottom left corner (Half)
            addArc(
                shadowBounds.left,
                shadowBounds.bottom - halfRadius,
                shadowBounds.left + doubleRadius,
                shadowBounds.bottom,
                90f,
                45f
            )
        }

        withClip(outlinePath) {
            paint.color = theme.darkColor
            drawPath(lightPath, paint)

            paint.color = theme.lightColor
            drawPath(darkPath, paint)
        }
    }
}