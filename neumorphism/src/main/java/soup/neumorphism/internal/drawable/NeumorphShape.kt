package soup.neumorphism.internal.drawable

import android.graphics.*
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable.NeumorphShapeDrawableState
import soup.neumorphism.internal.shape.Shape
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withClipOut
import kotlin.math.min

internal class NeumorphShape(
    private val appearance: NeumorphShadow.Style,
    private val theme: NeumorphShadow.Theme,
    private val isOuterShadow: Boolean
) : Shape {

    private var shadowBitmap: Bitmap? = null
    private var shadow: NeumorphShadow? = null

    override fun draw(canvas: Canvas, outlinePath: Path) {
        val bitmap = shadowBitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            return
        }

        val shadow = shadow
        if (shadow != null) {
            shadowBitmap = shadow.drawToBitmap()
            return draw(canvas, outlinePath)
        }
    }

    override fun updateShadowBitmap(bounds: Rect) {
        shadow = if (isOuterShadow) NeumorphOuterShadow(appearance, theme, bounds)
        else NeumorphInnerShadow(appearance, theme, bounds)
    }
}