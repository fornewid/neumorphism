package soup.neumorphism.internal.drawable

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.LruCache

object ShadowFactory {

    private const val KILO_BYTE = 1024

    // Use 1/6th of the available memory for this memory cache.
    private const val CACHE_SIZE_DIVIDER = 6

    private val reusable_shapes by lazy {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        val maxMemory = Runtime.getRuntime().maxMemory() / KILO_BYTE

        // Use 1/CACHE_SIZE_DIVIDER of the available memory for this memory cache.
        val cacheSize = maxMemory.toInt() / CACHE_SIZE_DIVIDER

        object : LruCache<Int, Bitmap>(cacheSize) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            override fun sizeOf(key: Int, bitmap: Bitmap): Int
                = bitmap.byteCount / KILO_BYTE
        }
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

    private fun Rect.calculateHashCode(): Int {
        var result = width().hashCode()
        result = 31 * result + height().hashCode()
        return result
    }
}