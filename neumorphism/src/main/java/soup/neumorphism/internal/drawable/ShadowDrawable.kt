package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.drawable.Drawable

class ShadowDrawable(
    elevation: Float,
    shadowColor: Int,
    private val coverage: ShadowCoverage
) : Drawable() {

    private var rectangleRectPaths: RectangleRectPaths? = null

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = elevation
        color = shadowColor
    }

    override fun draw(canvas: Canvas) {
        when(coverage) {
            is ShadowCoverage.Oval -> canvas.drawOvalShadow(coverage)
            is ShadowCoverage.Rectangle -> canvas.drawRectangleShadow(coverage)
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

    private fun Canvas.drawOvalShadow(coverage: ShadowCoverage.Oval) {

    }

    private fun Canvas.drawRectangleShadow(coverage: ShadowCoverage.Rectangle) {
        if (rectangleRectPaths == null) {
            val drawingRect = copyBounds()
            val bounds = RectF(drawingRect)
            rectangleRectPaths = RectangleRectPaths(coverage, bounds)
        }

        requireNotNull(rectangleRectPaths).apply {
            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_LINE)) {
                var startX = drawingRect.left
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_LEFT_CORNER)) {
                    startX += coverage.radius
                }

                var endX = drawingRect.right
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_RIGHT_CORNER)) {
                    endX -= coverage.radius
                }

                drawLine(
                    startX,
                    drawingRect.top,
                    endX,
                    drawingRect.top,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_RIGHT_CORNER)) {
                drawArc(
                    topRightArcBound,
                    -90f,
                    90f,
                    false,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.RIGHT_LINE)) {
                var startY = drawingRect.top
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_RIGHT_CORNER)) {
                    startY += coverage.radius
                }

                var endY = drawingRect.bottom
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_RIGHT_CORNER)) {
                    endY -= coverage.radius
                }

                drawLine(
                    drawingRect.right,
                    startY,
                    drawingRect.right,
                    endY,
                    paint
                )

                drawLine(
                    drawingRect.right,
                    drawingRect.top + coverage.radius,
                    drawingRect.right,
                    drawingRect.bottom - coverage.radius,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_RIGHT_CORNER)) {
                drawArc(
                    bottomRightArcBound,
                    0f,
                    90f,
                    false,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_LINE)) {
                var startX = drawingRect.left
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_LEFT_CORNER)) {
                    startX += coverage.radius
                }

                var endX = drawingRect.right
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_RIGHT_CORNER)) {
                    endX -= coverage.radius
                }

                drawLine(
                    startX,
                    drawingRect.bottom,
                    endX,
                    drawingRect.bottom,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_LEFT_CORNER)) {
                drawArc(
                    bottomLeftArcBound,
                    90f,
                    90f,
                    false,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.LEFT_LINE)) {
                var startY = drawingRect.top
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_LEFT_CORNER)) {
                    startY += coverage.radius
                }

                var endY = drawingRect.bottom
                if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.BOTTOM_LEFT_CORNER)) {
                    endY -= coverage.radius
                }

                drawLine(
                    drawingRect.left,
                    startY,
                    drawingRect.left,
                    endY,
                    paint
                )
            }

            if (coverage.hasCoverage(ShadowCoverage.Rectangle.Sides.TOP_LEFT_CORNER)) {
                drawArc(
                    topLeftArcBound,
                    180f,
                    90f,
                    false,
                    paint
                )
            }
        }
    }

    private class RectangleRectPaths(
        coverage: ShadowCoverage.Rectangle,
        val drawingRect: RectF
    ) {
        val topLeftArcBound = RectF()
        val topRightArcBound = RectF()
        val bottomLeftArcBound = RectF()
        val bottomRightArcBound = RectF()

        init {
            topRightArcBound[drawingRect.right - coverage.radius * 2, drawingRect.top, drawingRect.right] = drawingRect.top + coverage.radius * 2
            bottomRightArcBound[drawingRect.right - coverage.radius * 2, drawingRect.bottom - coverage.radius * 2, drawingRect.right] = drawingRect.bottom
            bottomLeftArcBound[drawingRect.left, drawingRect.bottom - coverage.radius * 2, drawingRect.left + coverage.radius * 2] = drawingRect.bottom
            topLeftArcBound[drawingRect.left, drawingRect.top, drawingRect.left + coverage.radius * 2] = drawingRect.top + coverage.radius * 2
        }
    }
}