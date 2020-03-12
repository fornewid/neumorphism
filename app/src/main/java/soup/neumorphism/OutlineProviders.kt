package soup.neumorphism

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

object OvalOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        outline.setOval(
            view.paddingLeft,
            view.paddingTop,
            view.width - view.paddingRight,
            view.height - view.paddingBottom
        )
    }
}

class RoundRectOutlineProvider(private val radius: Float) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(
            view.paddingLeft,
            view.paddingTop,
            view.width - view.paddingRight,
            view.height - view.paddingBottom,
            radius
        )
    }
}

fun View.setClipToOval(doClip: Boolean) {
    clipToOutline = doClip
    outlineProvider = if (doClip) OvalOutlineProvider else null
}

fun View.setClipToRoundRect(clipRadius: Float) {
    clipToOutline = true
    outlineProvider = RoundRectOutlineProvider(clipRadius)
}
