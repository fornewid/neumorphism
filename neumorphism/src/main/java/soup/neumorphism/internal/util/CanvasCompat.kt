package soup.neumorphism.internal.util

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.os.Build

internal object CanvasCompat {

    fun clipOutPath(canvas: Canvas, path: Path): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(path)
        } else {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        }
    }
}
