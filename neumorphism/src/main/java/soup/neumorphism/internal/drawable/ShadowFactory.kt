package soup.neumorphism.internal.drawable

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.LruCache
import soup.neumorphism.internal.util.calculateHashCode

object ShadowFactory {

    private const val KILO_BYTE = 1024

    // Use 100 MB for cache by default
    const val DEFAULT_CACHE_SIZE = 100 * KILO_BYTE

    private val reusableShapes by lazy {
        object : LruCache<Int, Bitmap>(DEFAULT_CACHE_SIZE) {
            // The cache size will be measured in kilobytes rather than number of items
            override fun sizeOf(key: Int, bitmap: Bitmap): Int
                = bitmap.byteCount / KILO_BYTE
        }
    }

    fun setCacheSizeInKiloBytes(cacheSize: Int) {
        reusableShapes.resize(cacheSize)
    }

    fun setCacheSizeInBytes(cacheSize: Long) {
        val sizeInKilos = (cacheSize / KILO_BYTE).toInt()
        reusableShapes.resize(sizeInKilos)
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

        return reusableShapes[hashCode] ?: createNewShadow(
            appearance,
            theme,
            isOuter,
            bounds
        ).also { newShape ->
            reusableShapes.put(hashCode, newShape)
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
}