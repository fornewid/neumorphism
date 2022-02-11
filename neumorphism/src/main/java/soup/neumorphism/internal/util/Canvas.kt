package soup.neumorphism.internal.util

import android.graphics.*
import android.os.Build

inline fun Bitmap.onCanvas(
    block: Canvas.() -> Unit
): Bitmap = also {
    Canvas(it).run(block)
}

inline fun Canvas.withTranslation(
    x: Float = 0f,
    y: Float = 0f,
    block: Canvas.() -> Unit
) {
    val checkpoint = save()
    translate(x, y)
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}

inline fun Canvas.withClip(
    clipPath: Path,
    block: Canvas.() -> Unit
) {
    val checkpoint = save()
    clipPath(clipPath)
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}

inline fun Canvas.withClipOut(
    clipPath: Path,
    block: Canvas.() -> Unit
) {
    val checkpoint = save()
    clipOutPath(this, clipPath)
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}

fun clipOutPath(canvas: Canvas, path: Path): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        canvas.clipOutPath(path)
    } else {
        canvas.clipPath(path, Region.Op.DIFFERENCE)
    }
}

fun Rect.calculateHashCode(): Int {
    var result = width().hashCode()
    result = 31 * result + height().hashCode()
    return result
}