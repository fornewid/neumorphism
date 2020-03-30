package soup.neumorphism.internal

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.os.Build

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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        clipOutPath(clipPath)
    } else {
        clipPath(clipPath, Region.Op.DIFFERENCE)
    }
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}
