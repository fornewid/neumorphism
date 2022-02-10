package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.Path.Direction.CW
import android.os.Build
import soup.neumorphism.CornerFamily
import soup.neumorphism.internal.util.onCanvas
import kotlin.math.min

abstract class NeumorphShadow(
    protected val appearance: Appearance,
    protected val theme: Theme,
    protected val bounds: Rect
) {

    protected val outlinePath get() = ShadowUtils.createPath(appearance, bounds)

    private val maxCornerRadius get() = min(
        bounds.width() / 2f,
        bounds.height() / 2f
    )

    protected val cornerRadius get() = when (appearance.cornerFamily) {
        CornerFamily.ROUNDED -> min(maxCornerRadius, appearance.cornerSize)
        CornerFamily.OVAL -> maxCornerRadius
    }

    protected abstract fun draw(canvas: Canvas)

    fun drawToBitmap(): Bitmap = createBitmap {
        draw(this)
    }

    private fun createBitmap(onCanvas: Canvas.() -> Unit): Bitmap {
        val offset = (appearance.radius + appearance.elevation) * 2

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

    data class Appearance(
        val elevation: Int,
        val radius: Int,
        val cornerFamily: CornerFamily,
        val cornerSize: Float
    )
}