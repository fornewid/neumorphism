package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import kotlin.math.pow

class ShadowDrawable(
    elevation: Float,
    private val radius: Float,
    shadowColor: Int
) : Drawable() {

    private var coverageFlags = Int.MAX_VALUE

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = elevation
        isAntiAlias = true
        color = shadowColor
    }

    override fun draw(canvas: Canvas) {
        val drawingRect = copyBounds()
        val topLeftArcBound = RectF()
        val topRightArcBound = RectF()
        val bottomLeftArcBound = RectF()
        val bottomRightArcBound = RectF()

        topRightArcBound[drawingRect.right - radius * 2, drawingRect.top.toFloat(), drawingRect.right.toFloat()] =
            drawingRect.top + radius * 2
        bottomRightArcBound[drawingRect.right - radius * 2, drawingRect.bottom - radius * 2, drawingRect.right.toFloat()] =
            drawingRect.bottom.toFloat()
        bottomLeftArcBound[drawingRect.left.toFloat(), drawingRect.bottom - radius * 2, drawingRect.left + radius * 2] =
            drawingRect.bottom.toFloat()
        topLeftArcBound[drawingRect.left.toFloat(), drawingRect.top.toFloat(), drawingRect.left + radius * 2] =
            drawingRect.top + radius * 2

        if (hasCoverage(Coverage.TOP_LINE)) {
            var startX = drawingRect.left.toFloat()
            if (hasCoverage(Coverage.TOP_LEFT_CORNER)) {
                startX += radius
            }

            var endX = drawingRect.right.toFloat()
            if (hasCoverage(Coverage.TOP_RIGHT_CORNER)) {
                endX -= radius
            }

            canvas.drawLine(
                startX,
                drawingRect.top.toFloat(),
                endX,
                drawingRect.top.toFloat(),
                paint
            )
        }

        if (hasCoverage(Coverage.TOP_RIGHT_CORNER)) {
            canvas.drawArc(
                topRightArcBound,
                -90f,
                90f,
                false,
                paint
            )
        }

        if (hasCoverage(Coverage.RIGHT_LINE)) {
            var startY = drawingRect.top.toFloat()
            if (hasCoverage(Coverage.TOP_RIGHT_CORNER)) {
                startY += radius
            }

            var endY = drawingRect.bottom.toFloat()
            if (hasCoverage(Coverage.BOTTOM_RIGHT_CORNER)) {
                endY -= radius
            }

            canvas.drawLine(
                drawingRect.right.toFloat(),
                startY,
                drawingRect.right.toFloat(),
                endY,
                paint
            )

            canvas.drawLine(
                drawingRect.right.toFloat(),
                drawingRect.top + radius,
                drawingRect.right.toFloat(),
                drawingRect.bottom - radius,
                paint
            )
        }

        if (hasCoverage(Coverage.BOTTOM_RIGHT_CORNER)) {
            canvas.drawArc(
                bottomRightArcBound,
                0f,
                90f,
                false,
                paint
            )
        }

        if (hasCoverage(Coverage.BOTTOM_LINE)) {
            var startX = drawingRect.left.toFloat()
            if (hasCoverage(Coverage.BOTTOM_LEFT_CORNER)) {
                startX += radius
            }

            var endX = drawingRect.right.toFloat()
            if (hasCoverage(Coverage.BOTTOM_RIGHT_CORNER)) {
                endX -= radius
            }

            canvas.drawLine(
                startX,
                drawingRect.bottom.toFloat(),
                endX,
                drawingRect.bottom.toFloat(),
                paint
            )
        }

        if (hasCoverage(Coverage.BOTTOM_LEFT_CORNER)) {
            canvas.drawArc(
                bottomLeftArcBound,
                90f,
                90f,
                false,
                paint
            )
        }

        if (hasCoverage(Coverage.LEFT_LINE)) {
            var startY = drawingRect.top.toFloat()
            if (hasCoverage(Coverage.TOP_LEFT_CORNER)) {
                startY += radius
            }

            var endY = drawingRect.bottom.toFloat()
            if (hasCoverage(Coverage.BOTTOM_LEFT_CORNER)) {
                endY -= radius
            }

            canvas.drawLine(
                drawingRect.left.toFloat(),
                startY,
                drawingRect.left.toFloat(),
                endY,
                paint
            )
        }

        if (hasCoverage(Coverage.TOP_LEFT_CORNER)) {
            canvas.drawArc(
                topLeftArcBound,
                180f,
                90f,
                false,
                paint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun setCoverage(vararg coverages: Coverage) {
        coverageFlags = 0
        coverages.forEach(::addCoverage)
    }

    fun hasCoverage(coverage: Coverage): Boolean {
        return coverageFlags and coverage.flag != 0
    }

    fun addCoverage(coverage: Coverage) {
        coverageFlags = coverageFlags or coverage.flag
    }

    fun removeCoverage(coverage: Coverage) {
        coverageFlags = coverageFlags and coverage.flag.inv()
    }

    enum class Coverage {
        TOP_LEFT_CORNER,
        TOP_LINE,
        TOP_RIGHT_CORNER,
        RIGHT_LINE,
        BOTTOM_RIGHT_CORNER,
        BOTTOM_LINE,
        BOTTOM_LEFT_CORNER,
        LEFT_LINE;

        val flag get() = 2.0.pow(ordinal).toInt()
    }
}