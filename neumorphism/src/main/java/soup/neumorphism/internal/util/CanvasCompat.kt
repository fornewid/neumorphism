package soup.neumorphism.internal.util

import android.graphics.*
import android.os.Build
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


internal object CanvasCompat {

    fun clipOutPath(canvas: Canvas, path: Path): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(path)
        } else {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        }
    }

    fun Canvas.drawCurvedArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        curveRadius: Float,
        paint: Paint
    ) {
        val path = Path()
        val midX = left + (right - left) / 2
        val midY = top + (bottom - top) / 2
        val xDiff = (midX - left)
        val yDiff = (midY - top)
        val angle = atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI) - 90
        val angleRadians = Math.toRadians(angle)
        val pointX = (midX + curveRadius * cos(angleRadians)).toFloat()
        val pointY = (midY + curveRadius * sin(angleRadians)).toFloat()
        path.moveTo(
            left,
            top
        )

        path.cubicTo(
            left,
            top,
            pointX,
            pointY,
            right,
            bottom
        )

        drawPath(path, paint)
    }
}
