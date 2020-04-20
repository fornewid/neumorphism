package soup.neumorphism

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes

class NeumorphShapeAppearanceModel {

    class Builder {

        @CornerFamily
        var cornerFamily: Int = CornerFamily.ROUNDED
        var cornerSize: Float = 0f

        fun setAllCorners(
            @CornerFamily cornerFamily: Int,
            @Dimension cornerSize: Float
        ): Builder {
            return setAllCorners(cornerFamily)
                .setAllCornerSizes(cornerSize)
        }

        fun setAllCorners(@CornerFamily cornerFamily: Int): Builder {
            return apply {
                this.cornerFamily = cornerFamily
            }
        }

        fun setAllCornerSizes(cornerSize: Float): Builder {
            return apply {
                this.cornerSize = cornerSize
            }
        }

        fun build(): NeumorphShapeAppearanceModel {
            return NeumorphShapeAppearanceModel(this)
        }
    }

    @CornerFamily
    private val cornerFamily: Int
    private val cornerSize: Float

    private constructor(builder: Builder) {
        cornerFamily = builder.cornerFamily
        cornerSize = builder.cornerSize
    }

    constructor() {
        cornerFamily = CornerFamily.ROUNDED
        cornerSize = 0f
    }

    @CornerFamily
    fun getCornerFamily(): Int {
        return cornerFamily
    }

    fun getCornerSize(): Float {
        return cornerSize
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
                val cornerSize =
                    getCornerSize(
                        a,
                        R.styleable.NeumorphShapeAppearance_neumorph_cornerSize,
                        defaultCornerSize
                    )
                return Builder()
                    .setAllCorners(cornerFamily, cornerSize)
            } finally {
                a.recycle()
            }
        }

        private fun getCornerSize(
            a: TypedArray, index: Int, defaultValue: Float
        ): Float {
            val value = a.peekValue(index) ?: return defaultValue
            return if (value.type == TypedValue.TYPE_DIMENSION) {
                TypedValue.complexToDimensionPixelSize(
                    value.data, a.resources.displayMetrics
                ).toFloat()
            } else {
                defaultValue
            }
        }
    }
}