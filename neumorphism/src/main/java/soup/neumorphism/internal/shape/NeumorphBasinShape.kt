package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import soup.neumorphism.internal.drawable.NeumorphShadow
import soup.neumorphism.internal.drawable.ShadowFactory

class NeumorphBasinShape(
    private val appearance: NeumorphShadow.Appearance,
    private val theme: NeumorphShadow.Theme,
    private val bounds: Rect
) : Shape {

    private val outerShadow get() = ShadowFactory.createReusableShadow(
        appearance,
        theme,
        isOuter = true,
        bounds
    )

    private val innerShadow get() = ShadowFactory.createReusableShadow(
        appearance,
        theme,
        isOuter = false,
        bounds
    )

    private val shadowPaint = Paint()
    override fun draw(canvas: Canvas, pressPercentage: Float) {
        val pressOffset = 1 - pressPercentage
        shadowPaint.alpha = (255 * pressOffset).toInt()
        canvas.drawBitmap(outerShadow, 0f, 0f, shadowPaint)
        canvas.drawBitmap(innerShadow, 0f, 0f, null)
    }
}
