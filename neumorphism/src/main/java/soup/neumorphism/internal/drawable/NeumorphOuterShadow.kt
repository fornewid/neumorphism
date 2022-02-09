package soup.neumorphism.internal.drawable

import android.graphics.Canvas
import android.graphics.Rect
import soup.neumorphism.NeumorphShapeDrawable
import soup.neumorphism.internal.util.withClipOut
import soup.neumorphism.internal.util.withTranslation

internal class NeumorphOuterShadow(
    state: NeumorphShapeDrawable.NeumorphShapeDrawableState,
    style: NeumorphShadowDrawable.Style,
    theme: NeumorphShadowDrawable.Theme,
    bounds: Rect
) : NeumorphShadow(state, style, theme, bounds) {

    override fun draw(canvas: Canvas) = with(canvas) {
        val margin = style.elevation.toFloat()

        resetOutlinePath()
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