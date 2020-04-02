package soup.neumorphism.internal.blur

import android.graphics.Color

internal data class BlurFactor(
    val width: Int,
    val height: Int,
    val radius: Int = DEFAULT_RADIUS,
    val sampling: Int = DEFAULT_SAMPLING,
    val color: Int = Color.TRANSPARENT
) {
    companion object {
        const val DEFAULT_RADIUS = 25
        const val DEFAULT_SAMPLING = 1
    }
}
