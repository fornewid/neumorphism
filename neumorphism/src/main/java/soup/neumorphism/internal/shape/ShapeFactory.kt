package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable
import soup.neumorphism.ShapeType
import soup.neumorphism.internal.shape.utils.SoftHashMap
import soup.neumorphism.internal.util.BitmapUtils.clipToRadius
import soup.neumorphism.internal.util.BitmapUtils.toBitmap
import java.lang.ref.SoftReference

internal object ShapeFactory {

    private const val MAX_CACHE_SIZE = 100

    private val reusable_shapes by lazy {
        SoftHashMap<Int, Shape>(MAX_CACHE_SIZE)
    }

    fun createNewShape(
        drawableState: NeumorphShapeDrawable.NeumorphShapeDrawableState,
        bounds: Rect
    ): Shape {
        val shape = when (val shapeType = drawableState.shapeType) {
            ShapeType.FLAT -> FlatPressableShape(drawableState)
            ShapeType.PRESSED -> PressedShape(drawableState)
            ShapeType.BASIN -> BasinShape(drawableState)
            else -> throw IllegalArgumentException("ShapeType($shapeType) is invalid.")
        }

        shape.updateShadowBitmap(bounds)
        return shape
    }

    fun createReusableShape(
            drawableState: NeumorphShapeDrawable.NeumorphShapeDrawableState,
            bounds: Rect
    ): Shape {
        var hashCode = drawableState.hashCode()
        hashCode = 31 * hashCode + bounds.calculateHashCode()

        return reusable_shapes[hashCode] ?: createNewShape(
            drawableState,
            bounds
        ).also { newShape ->
            reusable_shapes.put(hashCode, newShape)
        }
    }

    fun createNewBitmap(
        rect: RectF,
        cornerFamily: Int,
        cornerRadius: Float,
        drawable: Drawable
    ): Bitmap {
        val rectWidth = rect.width().toInt()
        val rectHeight = rect.height().toInt()
        val bitmap = drawable.toBitmap(rectWidth, rectHeight)

        val cornerSize = if (cornerFamily == CornerFamily.OVAL) bitmap.height / 2f
        else cornerRadius

        return bitmap.clipToRadius(cornerSize)
    }

    private fun Rect.calculateHashCode(): Int {
        var result = width().hashCode()
        result = 31 * result + height().hashCode()
        return result
    }
}