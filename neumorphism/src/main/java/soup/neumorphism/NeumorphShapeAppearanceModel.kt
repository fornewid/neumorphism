package soup.neumorphism

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import kotlin.math.min

class NeumorphShapeAppearanceModel {

    class Builder {

        @CornerFamily
        var cornerFamily: Int = CornerFamily.ROUNDED
        var topLeftCornerSize: Float = 0f
        var topRightCornerSize: Float = 0f
        var bottomLeftCornerSize: Float = 0f
        var bottomRightCornerSize: Float = 0f

        fun setAllCorners(
            @CornerFamily cornerFamily: Int,
            @Dimension cornerSize: Float
        ): Builder {
            return setAllCorners(cornerFamily)
                .setCornerRadius(cornerSize)
        }

        fun setAllCorners(@CornerFamily cornerFamily: Int): Builder {
            return apply {
                this.cornerFamily = cornerFamily
            }
        }

        fun setCornerRadius(cornerRadius: Float): Builder {
            return setTopLeftCornerSize(cornerRadius)
                .setTopRightCornerSize(cornerRadius)
                .setBottomLeftCornerSize(cornerRadius)
                .setBottomRightCornerSize(cornerRadius)
        }

        fun setTopLeftCornerSize(topLeftCornerSize: Float): Builder {
            return apply {
                this.topLeftCornerSize = topLeftCornerSize
            }
        }

        fun setTopRightCornerSize(topRightCornerSize: Float): Builder {
            return apply {
                this.topRightCornerSize = topRightCornerSize
            }
        }

        fun setBottomLeftCornerSize(bottomLeftCornerSize: Float): Builder {
            return apply {
                this.bottomLeftCornerSize = bottomLeftCornerSize
            }
        }

        fun setBottomRightCornerSize(bottomRightCornerSize: Float): Builder {
            return apply {
                this.bottomRightCornerSize = bottomRightCornerSize
            }
        }

        fun build(): NeumorphShapeAppearanceModel {
            return NeumorphShapeAppearanceModel(this)
        }
    }

    @CornerFamily
    private val cornerFamily: Int
    private val topLeftCornerSize: Float
    private val topRightCornerSize: Float
    private val bottomLeftCornerSize: Float
    private val bottomRightCornerSize: Float

    private constructor(builder: Builder) {
        cornerFamily = builder.cornerFamily
        topLeftCornerSize = builder.topLeftCornerSize
        topRightCornerSize = builder.topRightCornerSize
        bottomLeftCornerSize = builder.bottomLeftCornerSize
        bottomRightCornerSize = builder.bottomRightCornerSize
    }

    constructor() {
        cornerFamily = CornerFamily.ROUNDED
        topLeftCornerSize = 0f
        topRightCornerSize = 0f
        bottomLeftCornerSize = 0f
        bottomRightCornerSize = 0f
    }

    @CornerFamily
    fun getCornerFamily(): Int {
        return cornerFamily
    }

    fun getTopLeftCornerSize(): Float {
        return topLeftCornerSize
    }

    fun getTopRightCornerSize(): Float {
        return topRightCornerSize
    }

    fun getBottomLeftCornerSize(): Float {
        return bottomLeftCornerSize
    }

    fun getBottomRightCornerSize(): Float {
        return bottomRightCornerSize
    }

    internal fun getCornerRadii(maximum: Float): FloatArray {
        val topLeftCornerSize = min(maximum, getTopLeftCornerSize())
        val topRightCornerSize = min(maximum, getTopRightCornerSize())
        val bottomLeftCornerSize = min(maximum, getBottomLeftCornerSize())
        val bottomRightCornerSize = min(maximum, getBottomRightCornerSize())
        return floatArrayOf(
            topLeftCornerSize,
            topLeftCornerSize,
            topRightCornerSize,
            topRightCornerSize,
            bottomLeftCornerSize,
            bottomLeftCornerSize,
            bottomRightCornerSize,
            bottomRightCornerSize
        )
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }

        fun builder(
            context: Context,
            attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int,
            @StyleRes defStyleRes: Int,
            defaultCornerSize: Float = 0f
        ): Builder {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.NeumorphShape, defStyleAttr, defStyleRes
            )
            val shapeAppearanceResId = a.getResourceId(
                R.styleable.NeumorphShape_neumorph_shapeAppearance, 0
            )
            a.recycle()
            return builder(
                context,
                shapeAppearanceResId,
                defaultCornerSize
            )
        }

        private fun builder(
            context: Context,
            @StyleRes shapeAppearanceResId: Int,
            defaultCornerSize: Float
        ): Builder {
            val a = context.obtainStyledAttributes(
                shapeAppearanceResId,
                R.styleable.NeumorphShapeAppearance
            )
            try {
                val cornerFamily = a.getInt(
                    R.styleable.NeumorphShapeAppearance_neumorph_cornerFamily,
                    CornerFamily.ROUNDED
                )
                val cornerSize = a.getCornerSize(
                    R.styleable.NeumorphShapeAppearance_neumorph_cornerSize,
                    defaultCornerSize
                )
                val cornerSizeTopLeft = a.getCornerSize(
                    R.styleable.NeumorphShapeAppearance_neumorph_cornerSizeTopLeft,
                    cornerSize
                )
                val cornerSizeTopRight = a.getCornerSize(
                    R.styleable.NeumorphShapeAppearance_neumorph_cornerSizeTopRight,
                    cornerSize
                )
                val cornerSizeBottomRight = a.getCornerSize(
                    R.styleable.NeumorphShapeAppearance_neumorph_cornerSizeBottomLeft, cornerSize
                )
                val cornerSizeBottomLeft = a.getCornerSize(
                    R.styleable.NeumorphShapeAppearance_neumorph_cornerSizeBottomRight, cornerSize
                )
                return Builder()
                    .setAllCorners(cornerFamily)
                    .setTopLeftCornerSize(cornerSizeTopLeft)
                    .setTopRightCornerSize(cornerSizeTopRight)
                    .setBottomRightCornerSize(cornerSizeBottomRight)
                    .setBottomLeftCornerSize(cornerSizeBottomLeft)
            } finally {
                a.recycle()
            }
        }

        private fun TypedArray.getCornerSize(index: Int, defaultValue: Float): Float {
            val value = peekValue(index) ?: return defaultValue
            return if (value.type == TypedValue.TYPE_DIMENSION) {
                TypedValue.complexToDimensionPixelSize(
                    value.data, resources.displayMetrics
                ).toFloat()
            } else {
                defaultValue
            }
        }
    }
}