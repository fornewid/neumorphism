package soup.neumorphism.internal.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import soup.neumorphism.internal.util.SoftHashMap
import soup.neumorphism.internal.util.withClip

object ShadowFactory {

    private const val MAX_CACHE_SIZE = 200

    private val reusable_shapes by lazy {
        SoftHashMap<Int, Bitmap>(MAX_CACHE_SIZE)
    }

    private fun createNewShadow(
        appearance: NeumorphShadow.Appearance,
        theme: NeumorphShadow.Theme,
        isOuter: Boolean,
        bounds: Rect,
    ): Bitmap {
        val shape = if (isOuter) NeumorphOuterShadow(appearance, theme, bounds)
        else NeumorphInnerShadow(appearance, theme, bounds)

        return shape.drawToBitmap()
    }

    fun createReusableShadow(
        appearance: NeumorphShadow.Appearance,
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

    fun drawBackground(
        canvas: Canvas,
        appearance: NeumorphShadow.Appearance,
        bounds: Rect,
        drawable: Drawable
    ) {
        val path = ShadowUtils.createPath(appearance, bounds)
        drawable.bounds = bounds
        canvas.withClip(path) {
            drawable.draw(this)
        }
    }

    private fun Rect.calculateHashCode(): Int {
        var result = width().hashCode()
        result = 31 * result + height().hashCode()
        return result
    }
}