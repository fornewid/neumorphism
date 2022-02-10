package soup.neumorphism.internal.drawable

import android.graphics.Path
import android.graphics.Rect
import soup.neumorphism.CornerFamily

object ShadowUtils {

    fun createPath(
        appearance: NeumorphShadow.Appearance,
        bounds: Rect
    ): Path {
        val path = Path()

        val offset = appearance.elevation.toFloat() + appearance.radius
        val right = offset + bounds.width()
        val bottom = offset + bounds.height()

        when (appearance.cornerFamily) {
            CornerFamily.OVAL -> {
                path.addOval(offset, offset, right, bottom, Path.Direction.CW)
            }

            CornerFamily.ROUNDED -> {
                val cornerSize = appearance.cornerSize
                path.addRoundRect(offset, offset, right, bottom, cornerSize, cornerSize,
                    Path.Direction.CW
                )
            }
        }

        return path
    }
}