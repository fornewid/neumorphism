package soup.neumorphism.internal.drawable

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import soup.neumorphism.NeumorphShapeDrawable
import soup.neumorphism.internal.util.withClipOut
import soup.neumorphism.internal.util.withTranslation

internal class NeumorphOuterShadow(
    appearance: Style,
    theme: Theme,
    bounds: Rect
) : NeumorphShadow(appearance, theme, bounds) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(
            appearance.blurRadius.toFloat(),
            BlurMaskFilter.Blur.NORMAL
        )
    }

    override fun draw(canvas: Canvas) = with(canvas) {
        val margin = appearance.elevation.toFloat() / 2

        paint.maskFilter = BlurMaskFilter(
            appearance.blurRadius.toFloat(),
            BlurMaskFilter.Blur.NORMAL
        )

        withClipOut(outlinePath) {
            paint.color = theme.lightColor
            withTranslation(-margin, -margin) {
                drawPath(outlinePath, paint)
            }

            paint.color = theme.darkColor
            withTranslation(margin, margin) {
                drawPath(outlinePath, paint)
            }
        }
    }
}