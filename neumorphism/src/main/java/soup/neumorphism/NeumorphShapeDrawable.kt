package soup.neumorphism

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import soup.neumorphism.internal.blur.BlurProvider
import soup.neumorphism.internal.util.onCanvas
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withClipOut
import soup.neumorphism.internal.util.withTranslation
import kotlin.math.roundToInt

class NeumorphShapeDrawable : Drawable {

    private var drawableState: NeumorphShapeDrawableState

    private val boundsF = RectF()

    private var dirty = false

    private val outlinePath = Path()
    private val lightShadowDrawable = GradientDrawable()
    private val darkShadowDrawable = GradientDrawable()
    private var shadowBitmap: Bitmap? = null

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
    }

    override fun getConstantState(): ConstantState? {
        return drawableState
    }

    override fun mutate(): Drawable {
        val newDrawableState = NeumorphShapeDrawableState(drawableState)
        drawableState = newDrawableState
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
        when (drawableState.shapeAppearanceModel.getCorner()) {
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

    override fun draw(canvas: Canvas) {
        if (dirty) {
            calculateOutlinePath(boundsF, outlinePath)
            updateShadowBitmap(bounds)
            dirty = false
        }

        when (drawableState.shapeType) {
            ShapeType.PRESSED -> canvas.withClip(outlinePath) {
                shadowBitmap?.let {
                    canvas.drawBitmap(it, 0f, 0f, null)
                }
            }
            ShapeType.FLAT -> canvas.withClipOut(outlinePath) {
                shadowBitmap?.let {
                    val offset = (drawableState.shadowElevation * 2).unaryMinus()
                    canvas.drawBitmap(it, offset, offset, null)
                }
            }
        }
    }

    private fun updateShadowBitmap(bounds: Rect) {
        val w = bounds.width()
        val h = bounds.height()
        val shadowElevation = drawableState.shadowElevation.toInt()

        when (drawableState.shapeType) {
            ShapeType.PRESSED -> {
                lightShadowDrawable.apply {
                    setSize(w + shadowElevation, h + shadowElevation)
                    setStroke(shadowElevation, drawableState.shadowColorLight)

                    when (drawableState.shapeAppearanceModel.getCorner()) {
                        CornerFamily.OVAL -> {
                            shape = GradientDrawable.OVAL
                        }
                        CornerFamily.ROUNDED -> {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadii = drawableState.shapeAppearanceModel.getCornerSize().let {
                                floatArrayOf(0f, 0f, 0f, 0f, it, it, 0f, 0f)
                            }
                        }
                    }
                }
                darkShadowDrawable.apply {
                    setSize(w + shadowElevation, h + shadowElevation)
                    setStroke(shadowElevation, drawableState.shadowColorDark)

                    when (drawableState.shapeAppearanceModel.getCorner()) {
                        CornerFamily.OVAL -> {
                            shape = GradientDrawable.OVAL
                        }
                        CornerFamily.ROUNDED -> {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadii = drawableState.shapeAppearanceModel.getCornerSize().let {
                                floatArrayOf(it, it, 0f, 0f, 0f, 0f, 0f, 0f)
                            }
                        }
                    }
                }
            }
            ShapeType.FLAT -> {
                lightShadowDrawable.apply {
                    setColor(drawableState.shadowColorLight)
                    when (drawableState.shapeAppearanceModel.getCorner()) {
                        CornerFamily.OVAL -> {
                            shape = GradientDrawable.OVAL
                        }
                         CornerFamily.ROUNDED -> {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadii = drawableState.shapeAppearanceModel.getCornerSize().let {
                                floatArrayOf(it, it, it, it, it, it, it, it)
                            }
                        }
                    }
                }
                darkShadowDrawable.apply {
                    setColor(drawableState.shadowColorDark)

                    when (drawableState.shapeAppearanceModel.getCorner()) {
                        CornerFamily.OVAL -> {
                            shape = GradientDrawable.OVAL
                        }
                        CornerFamily.ROUNDED -> {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadii = drawableState.shapeAppearanceModel.getCornerSize().let {
                                floatArrayOf(it, it, it, it, it, it, it, it)
                            }
                        }
                    }
                }
            }
        }

        when (drawableState.shapeType) {
            ShapeType.PRESSED -> {
                val width: Int = w + shadowElevation
                val height: Int = h + shadowElevation
                lightShadowDrawable.setSize(width, height)
                lightShadowDrawable.setBounds(0, 0, width, height)
                darkShadowDrawable.setSize(width, height)
                darkShadowDrawable.setBounds(0, 0, width, height)
                shadowBitmap = generateShadowBitmap(w, h)
            }
            ShapeType.FLAT -> {
                lightShadowDrawable.setSize(w, h)
                lightShadowDrawable.setBounds(0, 0, w, h)
                darkShadowDrawable.setSize(w, h)
                darkShadowDrawable.setBounds(0, 0, w, h)
                shadowBitmap = generateShadowBitmap(w, h)
            }
        }
    }

    private fun generateShadowBitmap(w: Int, h: Int): Bitmap? {
        fun Bitmap.blurred(): Bitmap? {
            return drawableState.blurProvider.blur(this)
        }

        val shapeType = drawableState.shapeType
        val shadowElevation = drawableState.shadowElevation
        when (shapeType) {
            ShapeType.PRESSED -> {
                return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    .onCanvas {
                        withTranslation(-shadowElevation, -shadowElevation) {
                            lightShadowDrawable.draw(this)
                        }
                        darkShadowDrawable.draw(this)
                    }
                    .blurred()
            }
            ShapeType.FLAT -> {
                val width = (w + shadowElevation * 4).roundToInt()
                val height = (h + shadowElevation * 4).roundToInt()
                return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    .onCanvas {
                        withTranslation(shadowElevation, shadowElevation) {
                            lightShadowDrawable.draw(this)
                        }
                        withTranslation(shadowElevation * 3, shadowElevation * 3) {
                            darkShadowDrawable.draw(this)
                        }
                    }
                    .blurred()
            }
            else -> return null
        }
    }

    internal class NeumorphShapeDrawableState : ConstantState {

        var shapeAppearanceModel: NeumorphShapeAppearanceModel
        val blurProvider: BlurProvider

        var alpha = 255

        @ShapeType
        var shapeType: Int = ShapeType.FLAT
        var shadowElevation: Float = 0f
        var shadowColorLight: Int = Color.WHITE
        var shadowColorDark: Int = Color.BLACK

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
