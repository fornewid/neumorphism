package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import soup.neumorphism.internal.drawable.NeumorphInnerShadow
import soup.neumorphism.internal.drawable.NeumorphOuterShadow
import soup.neumorphism.internal.drawable.NeumorphShadow

internal class NeumorphShape(
    private val appearance: NeumorphShadow.Style,
    private val theme: NeumorphShadow.Theme,
    private val bounds: Rect,
    private val isOuterShadow: Boolean
) : Shape {

    private var shadowBitmap: Bitmap? = null
    private val shadow: NeumorphShadow by lazy {
        if (isOuterShadow) NeumorphOuterShadow(appearance, theme, bounds)
        else NeumorphInnerShadow(appearance, theme, bounds)
    }

    override fun draw(canvas: Canvas) {
        val bitmap = shadowBitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            return
        }

        shadowBitmap = shadow.drawToBitmap()
        return draw(canvas)
    }
}