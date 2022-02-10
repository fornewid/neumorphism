package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Rect
import soup.neumorphism.ShapeType
import soup.neumorphism.internal.drawable.NeumorphShadow
import soup.neumorphism.internal.drawable.ShadowFactory

class NeumorphBasinShape(
    appearance: NeumorphShadow.Style,
    theme: NeumorphShadow.Theme,
    bounds: Rect,
) : Shape {

    private val outerShadow = NeumorphFlatShape(appearance, theme, bounds)
    private val innerShadow = NeumorphPressedShape(appearance, theme, bounds)

    override fun draw(canvas: Canvas, pressPercentage: Float) {
        outerShadow.draw(canvas, pressPercentage)
        innerShadow.draw(canvas, pressPercentage)
    }
}
