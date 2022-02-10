package soup.neumorphism.internal.drawable

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import soup.neumorphism.CornerFamily
import soup.neumorphism.ShapeType
import soup.neumorphism.internal.shape.NeumorphBasinShape
import soup.neumorphism.internal.shape.NeumorphFlatShape
import soup.neumorphism.internal.shape.NeumorphPressedShape
import soup.neumorphism.internal.shape.Shape
import soup.neumorphism.internal.util.SoftHashMap
import soup.neumorphism.internal.util.BitmapUtils.clipToRadius
import soup.neumorphism.internal.util.BitmapUtils.toBitmap

object ShadowFactory {

    private const val MAX_CACHE_SIZE = 200

    private val reusable_shapes by lazy {
        SoftHashMap<Int, Bitmap>(MAX_CACHE_SIZE)
    }

    private fun createNewShadow(
        appearance: NeumorphShadow.Style,
        theme: NeumorphShadow.Theme,
        isOuter: Boolean,
        bounds: Rect,
    ): Bitmap {
        val shape = if (isOuter) NeumorphOuterShadow(appearance, theme, bounds)
        else NeumorphInnerShadow(appearance, theme, bounds)

        return shape.drawToBitmap()
    }

    fun createReusableShadow(
        appearance: NeumorphShadow.Style,
        theme: NeumorphShadow.Theme,
        isOuter: Boolean,
        bounds: Rect
    ): Bitmap {
        var hashCode = appearance.hashCode()
        hashCode = 31 * hashCode + theme.hashCode()
        hashCode = 31 * hashCode + isOuter.hashCode()
        hashCode = 31 * hashCode + bounds.calculateHashCode()

        return reusable_shapes[hashCode] ?: createNewShadow(
            appearance,
            theme,
            isOuter,
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