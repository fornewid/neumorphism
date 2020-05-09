package soup.neumorphism.internal.util

import android.content.Context
import android.content.res.TypedArray
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat

internal object NeumorphResources {

    fun getColor(
        context: Context,
        attributes: TypedArray,
        @StyleableRes index: Int,
        @ColorRes defaultColor: Int
    ): Int {
        return try {
            if (attributes.hasValue(index)) {
                val resourceId = attributes.getResourceId(index, 0)
                if (resourceId != 0) {
                    return ContextCompat.getColor(context, resourceId)
                }
            }
            attributes.getColor(index, ContextCompat.getColor(context, defaultColor))
        } catch (e: Exception) {
            ContextCompat.getColor(context, defaultColor)
        }
    }
}
