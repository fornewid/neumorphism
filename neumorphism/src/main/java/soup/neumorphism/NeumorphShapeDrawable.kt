package soup.neumorphism

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import soup.neumorphism.internal.blur.BlurProvider
import soup.neumorphism.internal.shape.BasinShape
import soup.neumorphism.internal.shape.FlatShape
import soup.neumorphism.internal.shape.PressedShape
import soup.neumorphism.internal.shape.Shape

class NeumorphShapeDrawable : Drawable {

    private var drawableState: NeumorphShapeDrawableState

    private val boundsF = RectF()

    private var dirty = false

    private val outlinePath = Path()
    private var shadow: Shape? = null

    constructor(context: Context) : this(NeumorphShapeAppearanceModel(), BlurProvider(context))

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : this(
        NeumorphShapeAppearanceModel.builder(context, attrs, defStyleAttr, defStyleRes).build(),
        BlurProvider(context)
    )

    internal constructor(
        shapeAppearanceModel: NeumorphShapeAppearanceModel,
        blurProvider: BlurProvider
    ) : this(NeumorphShapeDrawableState(shapeAppearanceModel, blurProvider))

    private constructor(drawableState: NeumorphShapeDrawableState) : super() {
        this.drawableState = drawableState
        this.shadow = shadowOf(drawableState.shapeType, drawableState)
    }

    private fun shadowOf(
        @ShapeType shapeType: Int,
        drawableState: NeumorphShapeDrawableState
    ): Shape {
        return when (shapeType) {
            ShapeType.FLAT -> FlatShape(drawableState)
            ShapeType.PRESSED -> PressedShape(drawableState)
            ShapeType.BASIN -> BasinShape(drawableState)
            else -> throw IllegalArgumentException("ShapeType($shapeType) is invalid.")
        }
    }

    override fun getConstantState(): ConstantState? {
        return drawableState
    }

    override fun mutate(): Drawable {
        val newDrawableState = NeumorphShapeDrawableState(drawableState)
        drawableState = newDrawableState
        shadow?.setDrawableState(newDrawableState)
        return this
    }

    fun setShapeAppearanceModel(shapeAppearanceModel: NeumorphShapeAppearanceModel) {
        drawableState.shapeAppearanceModel = shapeAppearanceModel
        invalidateSelf()
    }

    fun getShapeAppearanceModel(): NeumorphShapeAppearanceModel {
        return drawableState.shapeAppearanceModel
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        if (drawableState.alpha != alpha) {
            drawableState.alpha = alpha
            invalidateSelfIgnoreShape()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // not supported yet
    }

    fun setShapeType(@ShapeType shapeType: Int) {
        if (drawableState.shapeType != shapeType) {
            drawableState.shapeType = shapeType
            shadow = shadowOf(shapeType, drawableState)
            invalidateSelf()
        }
    }

    @ShapeType
    fun getShapeType(): Int {
        return drawableState.shapeType
    }

    fun setShadowElevation(shadowElevation: Float) {
        if (drawableState.shadowElevation != shadowElevation) {
            drawableState.shadowElevation = shadowElevation
            invalidateSelf()
        }
    }

    fun getShadowElevation(): Float {
        return drawableState.shadowElevation
    }

    fun setShadowColorLight(@ColorInt shadowColor: Int) {
        if (drawableState.shadowColorLight != shadowColor) {
            drawableState.shadowColorLight = shadowColor
            invalidateSelf()
        }
    }

    fun setShadowColorDark(@ColorInt shadowColor: Int) {
        if (drawableState.shadowColorDark != shadowColor) {
            drawableState.shadowColorDark = shadowColor
            invalidateSelf()
        }
    }

    fun getTranslationZ(): Float {
        return drawableState.translationZ
    }

    fun setTranslationZ(translationZ: Float) {
        if (drawableState.translationZ != translationZ) {
            drawableState.translationZ = translationZ
            invalidateSelfIgnoreShape()
        }
    }

    fun getZ(): Float {
        return getShadowElevation() + getTranslationZ()
    }

    fun setZ(z: Float) {
        setTranslationZ(z - getShadowElevation())
    }

    override fun invalidateSelf() {
        dirty = true
        super.invalidateSelf()
    }

    private fun invalidateSelfIgnoreShape() {
        super.invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        boundsF.set(bounds)
        dirty = true
    }

    fun getOutlinePath(): Path {
        return outlinePath
    }

    private fun calculateOutlinePath(bounds: RectF, path: Path) {
        val w = bounds.width()
        val h = bounds.height()
        path.reset()
        when (drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                path.addOval(0f, 0f, w, h, Path.Direction.CW)
            }
            CornerFamily.ROUNDED -> {
                val cornerSize = drawableState.shapeAppearanceModel.getCornerSize()
                path.addRoundRect(
                    0f, 0f, w, h,
                    cornerSize, cornerSize,
                    Path.Direction.CW
                )
            }
        }
        path.close()
    }

    override fun getOutline(outline: Outline) {
        when (drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                outline.setOval(bounds)
            }
            CornerFamily.ROUNDED -> {
                val cornerSize = drawableState.shapeAppearanceModel.getCornerSize()
                outline.setRoundRect(bounds, cornerSize)
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (dirty) {
            calculateOutlinePath(boundsF, outlinePath)
            shadow?.updateShadowBitmap(bounds)
            dirty = false
        }
        shadow?.draw(canvas, outlinePath)
    }

    internal class NeumorphShapeDrawableState : ConstantState {

        var shapeAppearanceModel: NeumorphShapeAppearanceModel
        val blurProvider: BlurProvider

        var alpha = 255

        @ShapeType
        var shapeType: Int = ShapeType.DEFAULT
        var shadowElevation: Float = 0f
        var shadowColorLight: Int = Color.WHITE
        var shadowColorDark: Int = Color.BLACK
        var translationZ = 0f

        constructor(
            shapeAppearanceModel: NeumorphShapeAppearanceModel,
            blurProvider: BlurProvider
        ) {
            this.shapeAppearanceModel = shapeAppearanceModel
            this.blurProvider = blurProvider
        }

        constructor(orig: NeumorphShapeDrawableState) {
            shapeAppearanceModel = orig.shapeAppearanceModel
            blurProvider = orig.blurProvider
            alpha = orig.alpha
            shapeType = orig.shapeType
            shadowElevation = orig.shadowElevation
            shadowColorLight = orig.shadowColorLight
            shadowColorDark = orig.shadowColorDark
        }

        override fun newDrawable(): Drawable {
            return NeumorphShapeDrawable(this).apply {
                // Force the calculation of the path for the new drawable.
                dirty = true
            }
        }

        override fun getChangingConfigurations(): Int {
            return 0
        }
    }
}
