package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import soup.neumorphism.internal.util.CanvasCompat.drawCurvedArc
import soup.neumorphism.internal.util.withTranslation

class NeumorphShadowDrawable(
    elevation: Float,
    private val lightShadowColor: Int,
    private val darkShadowColor: Int,
    private val coverage: ShadowCoverage
) : Drawable() {

    private var ovalRectPath: RectF? = null
    private val shadowOffset = elevation / 2

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
        val radius = coverage.radius
        val forthRadius = radius / 4
        val startX = shadowBounds.left + radius
        val endX = shadowBounds.right - radius
        val startY = shadowBounds.top + radius
        val endY = shadowBounds.bottom - radius

        //Light shadow drawing
        paint.color = lightShadowColor

        withTranslation(
            -shadowOffset,
            -shadowOffset
        ) {
            drawLine(shadowBounds.left, startY, shadowBounds.left, endY, paint)
            drawCurvedArc(shadowBounds.left, startY, startX, shadowBounds.top, radius, paint)
            drawLine(startX, shadowBounds.top, endX, shadowBounds.top, paint)

            drawCurvedArc(
                shadowBounds.left + forthRadius,
                shadowBounds.bottom - forthRadius,
                shadowBounds.left,
                endY,
                -forthRadius,
                paint
            )

            drawCurvedArc(
                endX,
                shadowBounds.top,
                shadowBounds.right - forthRadius,
                shadowBounds.top + forthRadius,
                -forthRadius,
                paint
            )
        }

        //Dark shadow
        paint.color = darkShadowColor

        withTranslation(
            -shadowOffset,
            -shadowOffset
        ) {
            drawCurvedArc(shadowBounds.right, endX, endX, shadowBounds.bottom, radius, paint)
            drawLine(startX, shadowBounds.bottom, endX, shadowBounds.bottom, paint)
            drawLine(shadowBounds.right, startY, shadowBounds.right, endY, paint)

            drawCurvedArc(
                shadowBounds.right - forthRadius,
                shadowBounds.top + forthRadius,
                shadowBounds.right,
                startY,
                -forthRadius,
                paint
            )

            drawCurvedArc(
                startX,
                shadowBounds.bottom,
                shadowBounds.left + forthRadius,
                shadowBounds.bottom - forthRadius,
                -forthRadius,
                paint
            )
        }
    }
}