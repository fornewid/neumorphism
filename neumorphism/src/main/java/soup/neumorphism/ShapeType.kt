package soup.neumorphism

import androidx.annotation.IntDef
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@IntDef(ShapeType.FLAT, ShapeType.PRESSED)
@Retention(AnnotationRetention.SOURCE)
annotation class ShapeType {
    companion object {
        const val FLAT = 0
        const val PRESSED = 1

        const val DEFAULT = FLAT
    }
}
