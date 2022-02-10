package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Rect
import soup.neumorphism.ShapeType
import soup.neumorphism.internal.drawable.NeumorphShadow

internal class BasinShape(
    appearance: NeumorphShadow.Style,
    theme: NeumorphShadow.Theme,
    bounds: Rect,
) : Shape {

    private val shadows by lazy {
        val outerShadow = ShapeFactory.createReusableShape(
            appearance,
            theme,
            ShapeType.FLAT,
            bounds
        )

        val innerShadow = ShapeFactory.createReusableShape(
            appearance,
            theme,
            ShapeType.PRESSED,
            bounds
        )

        listOf(outerShadow, innerShadow)
    }

    override fun draw(canvas: Canvas) {
        shadows.forEach { it.draw(canvas) }
    }
}
