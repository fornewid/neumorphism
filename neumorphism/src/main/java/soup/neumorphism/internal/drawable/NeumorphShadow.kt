package soup.neumorphism.internal.drawable

import android.graphics.*
import android.graphics.Path.Direction.CW
import soup.neumorphism.CornerFamily
import soup.neumorphism.NeumorphShapeDrawable
import soup.neumorphism.internal.util.onCanvas

internal abstract class NeumorphShadow(
    protected val state: NeumorphShapeDrawable.NeumorphShapeDrawableState,
    protected val style: NeumorphShadowDrawable.Style,
    protected val theme: NeumorphShadowDrawable.Theme,
    protected val bounds: Rect
) {

    protected abstract fun draw(canvas: Canvas)

    protected val paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
        }
    }

    protected val outlinePath = Path()

    fun drawToBitmap(): Bitmap? {
        val offset = (style.margin + style.elevation) * 2

        val width = bounds.width() + offset
        val height = bounds.height() + offset

        fun Bitmap.blurred(): Bitmap? {
            if (state.inEditMode) {
                return this
            }

            return state.blurProvider.blur(this)
        }

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .onCanvas {
                draw(this)
            }.blurred()
    }

    protected fun resetOutlinePath(extraOffset: Float = 0f) {
        val offset = state.shadowElevation.toFloat() + state.blurProvider.defaultBlurRadius + extraOffset
        val right = offset + bounds.width()
        val bottom = offset + bounds.height()

        outlinePath.reset()
        when (state.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                outlinePath.addOval(
                    offset,
                    offset,
                    right,
                    bottom,
                    CW
                )
            }

            CornerFamily.ROUNDED -> {
                val cornerSize = state.shapeAppearanceModel.getCornerSize()
                outlinePath.addRoundRect(
                    offset,
                    offset,
                    right,
                    bottom,
                    cornerSize,
                    cornerSize,
                    CW
                )
            }
        }

        outlinePath.close()
    }
}