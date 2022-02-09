package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.Path.Direction.CW
import android.os.Build
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable
import soup.neumorphism.internal.util.onCanvas

internal abstract class NeumorphShadow(
    protected val appearance: Style,
    protected val theme: Theme,
    protected val bounds: Rect
) {

    protected val outlinePath get() = Path().apply {
        val offset = appearance.elevation.toFloat() + appearance.blurRadius
        val right = offset + bounds.width()
        val bottom = offset + bounds.height()

        when (appearance.cornerFamily) {
            CornerFamily.OVAL -> {
                addOval(offset, offset, right, bottom, CW)
            }

            CornerFamily.ROUNDED -> {
                val cornerSize = appearance.cornerSize
                addRoundRect(offset, offset, right, bottom, cornerSize, cornerSize, CW)
            }
        }
    }

    protected val cornerRadius get() = when (appearance.cornerFamily) {
        CornerFamily.ROUNDED -> appearance.cornerSize
        CornerFamily.OVAL -> bounds.width() / 2f
    }

    protected abstract fun draw(canvas: Canvas)

    fun drawToBitmap(): Bitmap = createBitmap {
        draw(this)
    }

    private fun createBitmap(onCanvas: Canvas.() -> Unit): Bitmap {
        val offset = (appearance.blurRadius + appearance.elevation) * 2

        val width = bounds.width() + offset
        val height = bounds.height() + offset

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas(onCanvas).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    reconfigure(width, height, Bitmap.Config.HARDWARE)
                }
            }
    }

    data class Theme(
        val lightColor: Int,
        val darkColor: Int
    )

    data class Style(
        val elevation: Int,
        val blurRadius: Int,
        val cornerFamily: CornerFamily,
        val cornerSize: Float
    )
}