package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import soup.neumorphism.internal.drawable.NeumorphInnerShadow
import soup.neumorphism.internal.drawable.NeumorphOuterShadow
import soup.neumorphism.internal.drawable.NeumorphShadow

class NeumorphFlatShape(
    private val appearance: NeumorphShadow.Style,
    private val theme: NeumorphShadow.Theme,
    private val bounds: Rect
) : Shape {

    private val shadow: NeumorphShadow
        get() = NeumorphOuterShadow(appearance, theme, bounds)

    private val shadowBitmap: Bitmap by lazy {
        shadow.drawToBitmap()
    }

    private val shadowPaint = Paint()

    override fun draw(canvas: Canvas, pressPercentage: Float) {
        val pressOffset = 1 - pressPercentage
        shadowPaint.alpha = (255 * pressOffset).toInt()
        canvas.drawBitmap(
            shadowBitmap,
            0f,
            0f,
            shadowPaint
        )
    }
}