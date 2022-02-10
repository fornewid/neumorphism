package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import soup.neumorphism.ShapeType
import soup.neumorphism.internal.drawable.NeumorphShadow

class NeumorphBasinShape(
    private val appearance: NeumorphShadow.Style,
    private val theme: NeumorphShadow.Theme,
    private val bounds: Rect,
) : Shape {

    private val outerShadow get() = ShapeFactory.createReusableShape(
        appearance,
        theme,
        ShapeType.FLAT,
        bounds
    )

    private val innerShadow get() = ShapeFactory.createReusableShape(
        appearance,
        theme,
        ShapeType.PRESSED,
        bounds
    )

    override fun draw(canvas: Canvas, paint: Paint?) {
        outerShadow.draw(canvas, paint)
        innerShadow.draw(canvas, null)
    }
}
