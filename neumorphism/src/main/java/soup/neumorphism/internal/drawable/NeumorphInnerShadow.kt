package soup.neumorphism.internal.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import soup.neumorphism.NeumorphShapeDrawable
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withClipOut
import soup.neumorphism.internal.util.withTranslation

internal class NeumorphInnerShadow(
    state: NeumorphShapeDrawable.NeumorphShapeDrawableState,
    appearance: NeumorphShadowDrawable.Style,
    theme: NeumorphShadowDrawable.Theme,
    bounds: Rect
) : NeumorphShadow(state, appearance, theme, bounds) {

    override fun draw(canvas: Canvas) = with(canvas) {
        val margin = appearance.elevation.toFloat()

        resetOutlinePath()
        withClip(outlinePath) {
            paint.color = theme.darkColor
            resetOutlinePath(margin)
            withClipOut(outlinePath) {
                withTranslation(-margin, -margin) {
                    drawPath(outlinePath, paint)
                }
            }

            paint.color = theme.lightColor
            resetOutlinePath(-margin)
            withClipOut(outlinePath) {
                withTranslation(margin, margin) {
                    drawPath(outlinePath, paint)
                }
            }
        }
    }
}