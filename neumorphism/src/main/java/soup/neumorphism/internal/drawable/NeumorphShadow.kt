package soup.neumorphism.internal.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import soup.neumorphism.internal.util.withTranslation

class NeumorphShadow(
    private val style: NeumorphShadowDrawable.Style,
    private val theme: NeumorphShadowDrawable.Theme,
    private val shape: NeumorphShadowDrawable.Shape
) {
    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
        }
    }

    fun draw(canvas: Canvas, path: Path) {
        paint.color = theme.darkColor
        val margin = style.elevation.toFloat()

        canvas.withTranslation(
            -margin,
            -margin
        ) {
            drawPath(path, paint)
        }

        paint.color = theme.lightColor
        canvas.withTranslation(
            margin,
            margin
        ) {
            drawPath(path, paint)
        }
    }
}