package soup.neumorphism.internal.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect

interface Shape {
    fun draw(canvas: Canvas, paint: Paint?)
}
