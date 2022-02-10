package soup.neumorphism.internal.shape

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.ShapeType
import soup.neumorphism.internal.drawable.NeumorphShadow
import soup.neumorphism.internal.util.SoftHashMap
import soup.neumorphism.internal.util.BitmapUtils.clipToRadius
import soup.neumorphism.internal.util.BitmapUtils.toBitmap

internal object ShapeFactory {

    private const val MAX_CACHE_SIZE = 200

    private val reusable_shapes by lazy {
        SoftHashMap<Int, Shape>(MAX_CACHE_SIZE)
    }

    private fun createNewShape(
        appearance: NeumorphShadow.Style,
        theme: NeumorphShadow.Theme,
        shapeType: ShapeType,
        bounds: Rect,
    ): Shape {

        val shape = when (shapeType) {
            ShapeType.FLAT -> NeumorphShape(
                appearance,
                theme,
                bounds,
                isOuterShadow = true
            )

            ShapeType.PRESSED -> NeumorphShape(
                appearance,
                theme,
                bounds,
                isOuterShadow = false
            )

            ShapeType.BASIN -> BasinShape(
                appearance,
                theme,
                bounds
            )
        }

        return shape
    }

    fun createReusableShape(
        appearance: NeumorphShadow.Style,
        theme: NeumorphShadow.Theme,
        shapeType: ShapeType,
        bounds: Rect
    ): Shape {
        var hashCode = appearance.hashCode()
        hashCode = 31 * hashCode + bounds.calculateHashCode()
        hashCode = 31 * hashCode + theme.hashCode()
        hashCode = 31 * hashCode + shapeType.hashCode()

        return reusable_shapes[hashCode] ?: createNewShape(
            appearance,
            theme,
            shapeType,
            bounds
        ).also { newShape ->
            reusable_shapes.put(hashCode, newShape)
        }
    }

    fun createNewBitmap(
        rect: RectF,
        cornerFamily: CornerFamily,
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