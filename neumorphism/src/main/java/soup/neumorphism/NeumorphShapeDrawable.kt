package soup.neumorphism

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import soup.neumorphism.internal.drawable.NeumorphShadow
import soup.neumorphism.internal.shape.Shape
import soup.neumorphism.internal.drawable.ShadowUtils
import soup.neumorphism.internal.shape.NeumorphBasinShape
import soup.neumorphism.internal.shape.NeumorphFlatShape
import soup.neumorphism.internal.shape.NeumorphPressedShape
import soup.neumorphism.internal.util.withClip
import kotlin.math.abs


open class NeumorphShapeDrawable : Drawable {

    private var drawableState: NeumorphShapeDrawableState

    private var dirty = true

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.TRANSPARENT
    }

    val outlinePath get() = ShadowUtils.createPath(
        appearance,
        getBoundsInternal()
    )

    private val appearance get() = NeumorphShadow.Appearance(
        drawableState.shadowElevation,
        drawableState.shadowRadius,
        drawableState.shapeAppearanceModel.getCornerFamily(),
        drawableState.shapeAppearanceModel.getCornerSize()
    )

    private val theme get() =  NeumorphShadow.Theme(
        drawableState.shadowColorLight,
        drawableState.shadowColorDark
    )

    private val rectF = RectF()
    private var shadow: Shape? = null

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : this(NeumorphShapeAppearanceModel.builder(context, attrs, defStyleAttr, defStyleRes).build())

    internal constructor(shapeAppearanceModel: NeumorphShapeAppearanceModel)
            : this(NeumorphShapeDrawableState(shapeAppearanceModel))

    private constructor(drawableState: NeumorphShapeDrawableState) : super() {
        this.drawableState = drawableState
    }

    override fun getConstantState(): ConstantState? {
        return drawableState
    }

    override fun mutate(): Drawable {
        val newDrawableState = drawableState.copy()
        drawableState = newDrawableState
        return this
    }

    fun setShapeAppearanceModel(shapeAppearanceModel: NeumorphShapeAppearanceModel) {
        drawableState.shapeAppearanceModel = shapeAppearanceModel
        updateShadowShape()
        invalidateSelf()
    }

    fun getShapeAppearanceModel(): NeumorphShapeAppearanceModel {
        return drawableState.shapeAppearanceModel
    }

    fun setBackgroundDrawable(drawable: Drawable?) {
        this.drawableState.backgroundDrawable = drawable
        invalidateSelf()
    }

    fun setFillColor(fillColor: ColorStateList?) {
        if (drawableState.fillColor != fillColor) {
            drawableState.fillColor = fillColor
            onStateChange(state)
        }
    }

    fun getFillColor(): ColorStateList? {
        return drawableState.fillColor
    }

    fun setStrokeColor(strokeColor: ColorStateList?) {
        if (drawableState.strokeColor != strokeColor) {
            drawableState.strokeColor = strokeColor
            onStateChange(state)
        }
    }

    fun getStrokeColor(): ColorStateList? {
        return drawableState.strokeColor
    }

    fun setStroke(strokeWidth: Float, @ColorInt strokeColor: Int) {
        setStrokeWidth(strokeWidth)
        setStrokeColor(ColorStateList.valueOf(strokeColor))
    }

    fun setStroke(strokeWidth: Float, strokeColor: ColorStateList?) {
        setStrokeWidth(strokeWidth)
        setStrokeColor(strokeColor)
    }

    fun getStrokeWidth(): Float {
        return drawableState.strokeWidth
    }

    fun setStrokeWidth(strokeWidth: Float) {
        drawableState.strokeWidth = strokeWidth
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        if (drawableState.alpha != alpha) {
            drawableState.alpha = alpha
            invalidateSelf()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // not supported yet
    }

    private fun getBoundsInternal(): Rect {
        val offset = drawableState.shadowElevation + drawableState.shadowRadius
        val bounds = super.getBounds()
        return Rect(
            bounds.left + offset,
            bounds.top + offset,
            bounds.right - offset,
            bounds.bottom - offset
        )
    }

    private fun getBoundsAsRectF(): RectF {
        rectF.set(getBoundsInternal())
        return rectF
    }

    fun setShapeType(shapeType: ShapeType) {
        if (drawableState.shapeType != shapeType) {
            drawableState.shapeType = shapeType
            updateShadowShape()
            invalidateSelf()
        }
    }

    private fun updateShadowShape() {
        val internalBounds = getBoundsInternal()
        if (internalBounds.width() <= 0 || internalBounds.height() <= 0) {
            shadow = null
            return
        }

        shadow = when(drawableState.shapeType) {
            ShapeType.FLAT -> NeumorphFlatShape(appearance, theme, internalBounds)
            ShapeType.PRESSED -> NeumorphPressedShape(appearance, theme, internalBounds)
            ShapeType.BASIN -> NeumorphBasinShape(appearance, theme, internalBounds)
        }
    }

    fun getShapeType(): ShapeType {
        return drawableState.shapeType
    }

    fun setShadowElevation(shadowElevation: Int) {
        if (drawableState.shadowElevation != shadowElevation) {
            drawableState.shadowElevation = shadowElevation
            updateShadowShape()
            invalidateSelf()
        }
    }

    fun getShadowElevation(): Int {
        return drawableState.shadowElevation
    }

    fun setShadowRadius(shadowRadius: Int) {
        if (drawableState.shadowRadius != shadowRadius) {
            drawableState.shadowRadius = shadowRadius
            updateShadowShape()
            invalidateSelf()
        }
    }

    fun getShadowRadius(): Int {
        return drawableState.shadowRadius
    }

    fun setShadowColorLight(@ColorInt shadowColor: Int) {
        if (drawableState.shadowColorLight != shadowColor) {
            drawableState.shadowColorLight = shadowColor
            updateShadowShape()
            invalidateSelf()
        }
    }

    fun setBackgroundDrawableState(state: IntArray) {
        if (!drawableState.backgroundDrawable?.state.contentEquals(state)) {
            drawableState.backgroundDrawable?.state = state
            invalidateSelf()
        }
    }

    fun setShadowColorDark(@ColorInt shadowColor: Int) {
        if (drawableState.shadowColorDark != shadowColor) {
            drawableState.shadowColorDark = shadowColor
            updateShadowShape()
            invalidateSelf()
        }
    }

    fun getTranslationZ(): Float {
        return drawableState.translationZ
    }

    fun setTranslationZ(translationZ: Float) {
        if (drawableState.translationZ != translationZ) {
            drawableState.translationZ = translationZ
            invalidateSelf()
        }
    }

    fun getZ(): Float {
        return getShadowElevation() + getTranslationZ()
    }

    fun setZ(z: Float) {
        setTranslationZ(z - getShadowElevation())
    }

    fun getPaintStyle(): Paint.Style? {
        return drawableState.paintStyle
    }

    fun setPaintStyle(paintStyle: Paint.Style) {
        drawableState.paintStyle = paintStyle
        invalidateSelf()
    }

    private fun hasBackgroundBitmap(): Boolean {
        return drawableState.backgroundDrawable?.let(Drawable::isVisible) ?: false
    }

    private fun hasFill(): Boolean {
        return (drawableState.paintStyle === Paint.Style.FILL_AND_STROKE
                || drawableState.paintStyle === Paint.Style.FILL)
    }

    private fun hasStroke(): Boolean {
        return ((drawableState.paintStyle == Paint.Style.FILL_AND_STROKE
                || drawableState.paintStyle == Paint.Style.STROKE)
                && strokePaint.strokeWidth > 0)
    }

    override fun onBoundsChange(bounds: Rect) {
        dirty = true
        super.onBoundsChange(bounds)
    }

    override fun draw(canvas: Canvas) {
        val prevBackgroundAlpha = drawableState.backgroundDrawable?.alpha ?: 0
        drawableState.backgroundDrawable?.alpha = modulateAlpha(prevBackgroundAlpha, drawableState.alpha)

        val prevFillAlpha = fillPaint.alpha
        fillPaint.alpha = modulateAlpha(prevFillAlpha, drawableState.alpha)

        strokePaint.strokeWidth = drawableState.strokeWidth
        val prevStrokeAlpha = strokePaint.alpha
        strokePaint.alpha = modulateAlpha(prevStrokeAlpha, drawableState.alpha)

        if (dirty) {
            updateShadowShape()
            dirty = false
        }

        if (hasFill()) {
            drawFillShape(canvas)
        }

        if (hasBackgroundBitmap()) {
            drawBackgroundBitmap(canvas)
        }

        val minTranslationZ = -18.74f
        val pressPercentage = abs(drawableState.translationZ) / abs(minTranslationZ)
        shadow?.draw(canvas, pressPercentage)

        if (hasStroke()) {
            drawStrokeShape(canvas)
        }

        drawableState.backgroundDrawable?.alpha = prevBackgroundAlpha
        fillPaint.alpha = prevFillAlpha
        strokePaint.alpha = prevStrokeAlpha
    }

    private fun drawBackgroundBitmap(canvas: Canvas) {
        val drawable = drawableState.backgroundDrawable ?: return
        val backgroundBounds = getBoundsInternal()
        val outlinePath = ShadowUtils.createPath(appearance, backgroundBounds)
        drawable.bounds = backgroundBounds
        canvas.withClip(outlinePath) {
            drawable.draw(this)
        }
    }
    
    private fun drawFillShape(canvas: Canvas) {
        canvas.drawPath(outlinePath, fillPaint)
    }

    private fun drawStrokeShape(canvas: Canvas) {
        canvas.drawPath(outlinePath, strokePaint)
    }

    override fun getOutline(outline: Outline) {
        when (drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                outline.setOval(getBoundsInternal())
            }
            CornerFamily.ROUNDED -> {
                val cornerSize = drawableState.shapeAppearanceModel.getCornerSize()
                outline.setRoundRect(getBoundsInternal(), cornerSize)
            }
        }
    }

    override fun isStateful(): Boolean {
        return (super.isStateful()
                || drawableState.fillColor?.isStateful == true)
    }

    override fun onStateChange(state: IntArray): Boolean {
        val invalidateSelf = updateColorsForState(state)
        if (invalidateSelf) {
            invalidateSelf()
        }
        return invalidateSelf
    }

    private fun updateColorsForState(state: IntArray): Boolean {
        var invalidateSelf = false
        drawableState.fillColor?.let { fillColor ->
            val previousFillColor: Int = fillPaint.color
            val newFillColor: Int = fillColor.getColorForState(state, previousFillColor)
            if (previousFillColor != newFillColor) {
                fillPaint.color = newFillColor
                invalidateSelf = true
            }
        }
        drawableState.strokeColor?.let { strokeColor ->
            val previousStrokeColor = strokePaint.color
            val newStrokeColor = strokeColor.getColorForState(state, previousStrokeColor)
            if (previousStrokeColor != newStrokeColor) {
                strokePaint.color = newStrokeColor
                invalidateSelf = true
            }
        }
        return invalidateSelf
    }

    fun setInEditMode(inEditMode: Boolean) {
        drawableState.inEditMode = inEditMode
    }

    internal data class NeumorphShapeDrawableState(
        var shapeAppearanceModel: NeumorphShapeAppearanceModel,
        var inEditMode: Boolean = false,
        var backgroundDrawable: Drawable? = null,
        var fillColor: ColorStateList? = null,
        var strokeColor: ColorStateList? = null,
        var strokeWidth: Float = 0f,
        var alpha: Int = 255,
        var shapeType: ShapeType = ShapeType.FLAT,
        var shadowElevation: Int = 0,
        var shadowRadius: Int = 10,
        var shadowColorLight: Int = Color.WHITE,
        var shadowColorDark: Int = Color.BLACK,
        var translationZ: Float = 0f,
        var paintStyle: Paint.Style = Paint.Style.FILL_AND_STROKE
    ) : ConstantState() {

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

    companion object {

        private fun modulateAlpha(paintAlpha: Int, alpha: Int): Int {
            val scale = alpha + (alpha ushr 7) // convert to 0..256
            return paintAlpha * scale ushr 8
        }

    }
}
