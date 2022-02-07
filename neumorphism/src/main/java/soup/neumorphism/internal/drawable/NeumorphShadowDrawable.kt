package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.drawable.Drawable

class NeumorphShadowDrawable(
    elevation: Float,
    private val lightShadowColor: Int,
    private val darkShadowColor: Int,
    private val coverage: ShadowCoverage
) : Drawable() {

    private var rectangleRectPaths: RectangleRectPaths? = null
    private var ovalRectPath: RectF? = null

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = elevation
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
        if (ovalRectPath == null) {
            ovalRectPath = RectF(bounds)
        }

        drawArc(
            requireNotNull(ovalRectPath),
            coverage.startAngle,
            180f,
            false,
            paint
        )
    }

    private fun Canvas.drawRectangleShadow(coverage: ShadowCoverage.Rectangle) {
        if (rectangleRectPaths == null) {
            val drawingRect = copyBounds()
            val bounds = RectF(drawingRect)
            rectangleRectPaths = RectangleRectPaths(coverage, bounds)
        }

        requireNotNull(rectangleRectPaths).apply {
            //Light shadow drawing
            paint.color = lightShadowColor

            drawArc(
                topLeftArcBound,
                225f,
                45f,
                false,
                paint
            )

            var startX = drawingRect.left + coverage.radius
            var endX = drawingRect.right - coverage.radius

            drawLine(
                startX,
                drawingRect.top,
                endX,
                drawingRect.top,
                paint
            )

            drawArc(
                topRightArcBound,
                -90f,
                90f,
                false,
                paint
            )

            var startY = drawingRect.top + coverage.radius
            var endY = drawingRect.bottom - coverage.radius

            drawLine(
                drawingRect.right,
                startY,
                drawingRect.right,
                endY,
                paint
            )

            drawArc(
                bottomRightArcBound,
                0f,
                45f,
                false,
                paint
            )


            //Dark shadow
            paint.color = darkShadowColor

            drawArc(
                bottomRightArcBound,
                45f,
                45f,
                false,
                paint
            )

            startX = drawingRect.left + coverage.radius
            endX = drawingRect.right - coverage.radius

            drawLine(
                startX,
                drawingRect.bottom,
                endX,
                drawingRect.bottom,
                paint
            )

            drawArc(
                bottomLeftArcBound,
                90f,
                90f,
                false,
                paint
            )

            startY = drawingRect.top + coverage.radius
            endY = drawingRect.bottom - coverage.radius

            drawLine(
                drawingRect.left,
                startY,
                drawingRect.left,
                endY,
                paint
            )

            drawArc(
                topLeftArcBound,
                180f,
                45f,
                false,
                paint
            )
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