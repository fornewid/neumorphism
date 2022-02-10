package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect

internal interface Shape {
    fun draw(canvas: Canvas)
}
