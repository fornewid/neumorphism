package soup.neumorphism.internal.drawable

import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.Rect
import soup.neumorphism.CornerFamily
import kotlin.math.min

object ShadowUtils {

    fun createPath(
        appearance: NeumorphShadow.Appearance,
        bounds: Rect
    ): Path {
        val offset = appearance.elevation.toFloat() + appearance.radius
        val right = offset + bounds.width()
        val bottom = offset + bounds.height()

        val maxCornerRadius = min(
        bounds.width() / 2f,
        bounds.height() / 2f
        )

        val cornerRadius = when (appearance.cornerFamily) {
            CornerFamily.ROUNDED -> min(maxCornerRadius, appearance.cornerSize)
            CornerFamily.OVAL -> maxCornerRadius
        }

        return Path().apply {
            addRoundRect(offset, offset, right, bottom, cornerRadius, cornerRadius, CW)
        }
    }
}