package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Rect
import soup.neumorphism.internal.drawable.NeumorphShadow
import soup.neumorphism.internal.drawable.ShadowFactory

class NeumorphPressedShape(
    private val appearance: NeumorphShadow.Style,
    private val theme: NeumorphShadow.Theme,
    private val bounds: Rect
) : Shape {

    private val shadowBitmap get() = ShadowFactory.createReusableShadow(
        appearance,
        theme,
        isOuter = false,
        bounds
    )

    override fun draw(canvas: Canvas, pressPercentage: Float) {
        canvas.drawBitmap(shadowBitmap, 0f, 0f, null)
    }
}