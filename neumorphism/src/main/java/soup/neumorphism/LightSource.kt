package soup.neumorphism

import androidx.annotation.IntDef
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@IntDef(
    LightSource.LEFT_TOP,
    LightSource.LEFT_BOTTOM,
    LightSource.RIGHT_TOP,
    LightSource.RIGHT_BOTTOM
)
@Retention(AnnotationRetention.SOURCE)
annotation class LightSource {
    companion object {
        const val LEFT_TOP = 0
        const val LEFT_BOTTOM = 1
        const val RIGHT_TOP = 2
        const val RIGHT_BOTTOM = 3

        const val DEFAULT = LEFT_TOP

        fun isLeft(@LightSource lightSource: Int): Boolean {
            return lightSource == LEFT_TOP || lightSource == LEFT_BOTTOM
        }

        fun isTop(@LightSource lightSource: Int): Boolean {
            return lightSource == LEFT_TOP || lightSource == RIGHT_TOP
        }

        fun isRight(@LightSource lightSource: Int): Boolean {
            return lightSource == RIGHT_TOP || lightSource == RIGHT_BOTTOM
        }

        fun isBottom(@LightSource lightSource: Int): Boolean {
            return lightSource == LEFT_BOTTOM || lightSource == RIGHT_BOTTOM
        }
    }
}
