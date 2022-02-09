package soup.neumorphism.internal.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.os.Build

internal inline fun Bitmap.onCanvas(
    block: Canvas.() -> Unit
): Bitmap = also {
    Canvas(it).run(block)
}

internal inline fun Canvas.withTranslation(
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

internal inline fun Canvas.withClip(
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

internal inline fun Canvas.withClipOut(
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
