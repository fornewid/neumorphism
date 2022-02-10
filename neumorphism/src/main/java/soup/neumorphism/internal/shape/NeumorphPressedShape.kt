package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import soup.neumorphism.internal.drawable.NeumorphInnerShadow
import soup.neumorphism.internal.drawable.NeumorphOuterShadow
import soup.neumorphism.internal.drawable.NeumorphShadow

class NeumorphPressedShape(
    private val appearance: NeumorphShadow.Style,
    private val theme: NeumorphShadow.Theme,
    private val bounds: Rect
) : Shape {

    private val shadow: NeumorphShadow
        get() = NeumorphInnerShadow(appearance, theme, bounds)

    private val shadowBitmap: Bitmap by lazy {
        shadow.drawToBitmap()
    }

    override fun draw(canvas: Canvas, pressPercentage: Float) {
        canvas.drawBitmap(shadowBitmap, 0f, 0f, null)
    }
}