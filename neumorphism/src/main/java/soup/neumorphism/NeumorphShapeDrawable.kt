package soup.neumorphism

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import soup.neumorphism.internal.blur.BlurProvider
import soup.neumorphism.internal.shape.Shape
import soup.neumorphism.internal.shape.ShapeFactory
import soup.neumorphism.internal.util.BitmapUtils.clipToRadius
import soup.neumorphism.internal.util.BitmapUtils.toBitmap


class NeumorphShapeDrawable : Drawable {

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

    private val rectF = RectF()

    private val outlinePath = Path()
    private var shadow: Shape? = null

    private var backgroundRect: RectF? = null
    private var backgroundBitmap: Bitmap? = null

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
        shadow?.setDrawableState(newDrawableState)
        return this
    }

    fun setShapeAppearanceModel(shapeAppearanceModel: NeumorphShapeAppearanceModel) {
        drawableState.shapeAppearanceModel = shapeAppearanceModel
        updateShadowShape()
        updateBackgroundBitmap()
        invalidateSelf()
    }

    fun getShapeAppearanceModel(): NeumorphShapeAppearanceModel {
        return drawableState.shapeAppearanceModel
    }

    fun setBackgroundDrawable(drawable: Drawable?) {
        this.drawableState.backgroundDrawable = drawable
        updateBackgroundBitmap()
        invalidateSelf()
    }

    fun getBackgroundDrawable(): Drawable? {
        return this.drawableState.backgroundDrawable
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
        return drawableState.inset.let { inset ->
            val bounds = super.getBounds()
            Rect(
                bounds.left + inset.left,
                bounds.top + inset.top,
                bounds.right - inset.right,
                bounds.bottom - inset.bottom
            )
        }
    }

    private fun getBoundsAsRectF(): RectF {
        rectF.set(getBoundsInternal())
        return rectF
    }

    fun setInset(left: Int, top: Int, right: Int, bottom: Int) {
        drawableState.inset.set(left, top, right, bottom)
        updateShadowShape()
        invalidateSelf()
    }

    fun setShapeType(@ShapeType shapeType: Int) {
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

        shadow = ShapeFactory.createReusableShape(drawableState, internalBounds)
    }

    @ShapeType
    fun getShapeType(): Int {
        return drawableState.shapeType
    }

    fun setShadowElevation(shadowElevation: Float) {
        if (drawableState.shadowElevation != shadowElevation) {
            drawableState.shadowElevation = shadowElevation
            updateShadowShape()
            invalidateSelf()
        }
    }

    fun getShadowElevation(): Float {
        return drawableState.shadowElevation
    }

    fun setShadowColorLight(@ColorInt shadowColor: Int) {
        if (drawableState.shadowColorLight != shadowColor) {
            drawableState.shadowColorLight = shadowColor
            updateShadowShape()
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
            calculateOutlinePath(getBoundsAsRectF(), outlinePath)
            updateShadowShape()
            updateBackgroundBitmap()
            dirty = false
        }

        if (hasFill()) {
            drawFillShape(canvas)
        }

        if (hasBackgroundBitmap()) {
            drawBackgroundBitmap(canvas)
        }

        shadow?.draw(canvas, outlinePath)

        if (hasStroke()) {
            drawStrokeShape(canvas)
        }

        drawableState.backgroundDrawable?.alpha = prevBackgroundAlpha
        fillPaint.alpha = prevFillAlpha
        strokePaint.alpha = prevStrokeAlpha
    }

    private fun drawBackgroundBitmap(canvas: Canvas) {
        val rect = backgroundRect ?: return
        val bitmap = backgroundBitmap ?: return


        canvas.drawBitmap(bitmap, rect.left, rect.top, null)
    }

    private fun updateBackgroundBitmap() {
        val drawable = drawableState.backgroundDrawable ?: return
        val rect = RectF()
        outlinePath.computeBounds(rect, true)

        if (rect.width() <= 0 || rect.height() <= 0) {
            backgroundRect = null
            backgroundBitmap = null
            return
        }
        
        backgroundRect = rect
        backgroundBitmap = ShapeFactory.createReusableBitmap(
                rect,
                drawableState.shapeAppearanceModel.getCornerFamily(),
                drawableState.shapeAppearanceModel.getCornerSize(),
                drawable
        )
    }

    private fun drawFillShape(canvas: Canvas) {
        canvas.drawPath(outlinePath, fillPaint)
    }

    private fun drawStrokeShape(canvas: Canvas) {
        canvas.drawPath(outlinePath, strokePaint)
    }

    fun getOutlinePath(): Path {
        return outlinePath
    }

    private fun calculateOutlinePath(bounds: RectF, path: Path) {
        val left = drawableState.inset.left.toFloat()
        val top = drawableState.inset.top.toFloat()
        val right = left + bounds.width()
        val bottom = top + bounds.height()
        path.reset()
        when (drawableState.shapeAppearanceModel.getCornerFamily()) {
            CornerFamily.OVAL -> {
                path.addOval(left, top, right, bottom, Path.Direction.CW)
            }
            CornerFamily.ROUNDED -> {
                val cornerSize = drawableState.shapeAppearanceModel.getCornerSize()
                path.addRoundRect(
                    left, top, right, bottom,
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

    internal class NeumorphShapeDrawableState : ConstantState {

        var shapeAppearanceModel: NeumorphShapeAppearanceModel
        val blurProvider: BlurProvider
        var inEditMode: Boolean = false

        var inset: Rect = Rect()
        var backgroundDrawable: Drawable? = null
        var fillColor: ColorStateList? = null
        var strokeColor: ColorStateList? = null
        var strokeWidth = 0f

        var alpha = 255

        @ShapeType
        var shapeType: Int = ShapeType.DEFAULT
        var shadowElevation: Float = 0f
        var shadowColorLight: Int = Color.WHITE
        var shadowColorDark: Int = Color.BLACK
        var translationZ = 0f

        var paintStyle: Paint.Style = Paint.Style.FILL_AND_STROKE

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
            inEditMode = orig.inEditMode
            inset = Rect(orig.inset)
            backgroundDrawable = orig.backgroundDrawable
            fillColor = orig.fillColor
            strokeColor = orig.strokeColor
            strokeWidth = orig.strokeWidth
            alpha = orig.alpha
            shapeType = orig.shapeType
            shadowElevation = orig.shadowElevation
            shadowColorLight = orig.shadowColorLight
            shadowColorDark = orig.shadowColorDark
            translationZ = orig.translationZ
            paintStyle = orig.paintStyle
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

        override fun hashCode(): Int {
            var result = shapeAppearanceModel.hashCode()
            result = 31 * result + blurProvider.hashCode()
            result = 31 * result + inEditMode.hashCode()
            result = 31 * result + inset.hashCode()
            result = 31 * result + (backgroundDrawable?.hashCode() ?: 0)
            result = 31 * result + (fillColor?.hashCode() ?: 0)
            result = 31 * result + (strokeColor?.hashCode() ?: 0)
            result = 31 * result + strokeWidth.hashCode()
            result = 31 * result + alpha
            result = 31 * result + shapeType
            result = 31 * result + shadowElevation.hashCode()
            result = 31 * result + shadowColorLight
            result = 31 * result + shadowColorDark
            result = 31 * result + translationZ.hashCode()
            result = 31 * result + paintStyle.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NeumorphShapeDrawableState

            if (shapeAppearanceModel != other.shapeAppearanceModel) return false
            if (blurProvider != other.blurProvider) return false
            if (inEditMode != other.inEditMode) return false
            if (inset != other.inset) return false
            if (backgroundDrawable != other.backgroundDrawable) return false
            if (fillColor != other.fillColor) return false
            if (strokeColor != other.strokeColor) return false
            if (strokeWidth != other.strokeWidth) return false
            if (alpha != other.alpha) return false
            if (shapeType != other.shapeType) return false
            if (shadowElevation != other.shadowElevation) return false
            if (shadowColorLight != other.shadowColorLight) return false
            if (shadowColorDark != other.shadowColorDark) return false
            if (translationZ != other.translationZ) return false
            if (paintStyle != other.paintStyle) return false

            return true
        }

    }

    companion object {

        private fun modulateAlpha(paintAlpha: Int, alpha: Int): Int {
            val scale = alpha + (alpha ushr 7) // convert to 0..256
            return paintAlpha * scale ushr 8
        }

    }
}
