package soup.neumorphism

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import soup.neumorphism.internal.util.NeumorphResources

class NeumorphCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.neumorphCardViewStyle,
    defStyleRes: Int = R.style.Widget_Neumorph_CardView
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var isInitialized: Boolean = false
    private val shapeDrawable: NeumorphShapeDrawable

    private var insetStart = 0
    private var insetEnd = 0
    private var insetTop = 0
    private var insetBottom = 0

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NeumorphCardView, defStyleAttr, defStyleRes
        )
        val fillColor = a.getColorStateList(R.styleable.NeumorphCardView_neumorph_backgroundColor)
        val strokeColor = a.getColorStateList(R.styleable.NeumorphCardView_neumorph_strokeColor)
        val strokeWidth = a.getDimension(R.styleable.NeumorphCardView_neumorph_strokeWidth, 0f)
        val lightSource = a.getInt(R.styleable.NeumorphCardView_neumorph_lightSource, LightSource.DEFAULT)
        val shapeType = a.getInt(R.styleable.NeumorphCardView_neumorph_shapeType, ShapeType.DEFAULT)
        val inset = a.getDimensionPixelSize(
            R.styleable.NeumorphCardView_neumorph_inset, 0
        )
        val insetStart = a.getDimensionPixelSize(
            R.styleable.NeumorphCardView_neumorph_insetStart, -1
        )
        val insetEnd = a.getDimensionPixelSize(
            R.styleable.NeumorphCardView_neumorph_insetEnd, -1
        )
        val insetTop = a.getDimensionPixelSize(
            R.styleable.NeumorphCardView_neumorph_insetTop, -1
        )
        val insetBottom = a.getDimensionPixelSize(
            R.styleable.NeumorphCardView_neumorph_insetBottom, -1
        )
        val shadowElevation = a.getDimension(
            R.styleable.NeumorphCardView_neumorph_shadowElevation, 0f
        )
        val shadowColorLight = NeumorphResources.getColor(
            context, a,
            R.styleable.NeumorphCardView_neumorph_shadowColorLight,
            R.color.design_default_color_shadow_light
        )
        val shadowColorDark = NeumorphResources.getColor(
            context, a,
            R.styleable.NeumorphCardView_neumorph_shadowColorDark,
            R.color.design_default_color_shadow_dark
        )
        a.recycle()

        shapeDrawable = NeumorphShapeDrawable(context, attrs, defStyleAttr, defStyleRes).apply {
            setInEditMode(isInEditMode)
            setLightSource(lightSource)
            setShapeType(shapeType)
            setShadowElevation(shadowElevation)
            setShadowColorLight(shadowColorLight)
            setShadowColorDark(shadowColorDark)
            setFillColor(fillColor)
            setStroke(strokeWidth, strokeColor)
            setTranslationZ(translationZ)
        }
        internalSetInset(
            if (insetStart >= 0) insetStart else inset,
            if (insetTop >= 0) insetTop else inset,
            if (insetEnd >= 0) insetEnd else inset,
            if (insetBottom >= 0) insetBottom else inset
        )
        setBackgroundInternal(shapeDrawable)
        isInitialized = true
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        //TODO: clip using Outline smoothly
        val checkpoint = canvas.save()
        canvas.clipPath(shapeDrawable.getOutlinePath())
        try {
            return super.drawChild(canvas, child, drawingTime)
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    override fun setBackground(drawable: Drawable?) {
        setBackgroundDrawable(drawable)
    }

    override fun setBackgroundDrawable(drawable: Drawable?) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.")
    }

    private fun setBackgroundInternal(drawable: Drawable?) {
        super.setBackgroundDrawable(drawable)
    }

    fun setShapeAppearanceModel(shapeAppearanceModel: NeumorphShapeAppearanceModel) {
        shapeDrawable.setShapeAppearanceModel(shapeAppearanceModel)
    }

    fun getShapeAppearanceModel(): NeumorphShapeAppearanceModel {
        return shapeDrawable.getShapeAppearanceModel()
    }

    override fun setBackgroundColor(color: Int) {
        shapeDrawable.setFillColor(ColorStateList.valueOf(color))
    }

    fun setBackgroundColor(backgroundColor: ColorStateList?) {
        shapeDrawable.setFillColor(backgroundColor)
    }

    fun getBackgroundColor(): ColorStateList? {
        return shapeDrawable.getFillColor()
    }

    fun setStrokeColor(strokeColor: ColorStateList?) {
        shapeDrawable.setStrokeColor(strokeColor)
    }

    fun getStrokeColor(): ColorStateList? {
        return shapeDrawable.getStrokeColor()
    }

    fun setStrokeWidth(strokeWidth: Float) {
        shapeDrawable.setStrokeWidth(strokeWidth)
    }

    fun getStrokeWidth(): Float {
        return shapeDrawable.getStrokeWidth()
    }

    fun setLightSource(@LightSource lightSource: Int) {
        shapeDrawable.setLightSource(lightSource)
    }

    @LightSource
    fun getLightSource(): Int {
        return shapeDrawable.getLightSource()
    }

    fun setShapeType(@ShapeType shapeType: Int) {
        shapeDrawable.setShapeType(shapeType)
    }

    @ShapeType
    fun getShapeType(): Int {
        return shapeDrawable.getShapeType()
    }

    fun setInset(left: Int, top: Int, right: Int, bottom: Int) {
        internalSetInset(left, top, right, bottom)
    }

    private fun internalSetInset(left: Int, top: Int, right: Int, bottom: Int) {
        var changed = false
        if (insetStart != left) {
            changed = true
            insetStart = left
        }
        if (insetTop != top) {
            changed = true
            insetTop = top
        }
        if (insetEnd != right) {
            changed = true
            insetEnd = right
        }
        if (insetBottom != bottom) {
            changed = true
            insetBottom = bottom
        }

        if (changed) {
            shapeDrawable.setInset(left, top, right, bottom)
            requestLayout()
            invalidateOutline()
        }
    }

    fun setShadowElevation(shadowElevation: Float) {
        shapeDrawable.setShadowElevation(shadowElevation)
    }

    fun getShadowElevation(): Float {
        return shapeDrawable.getShadowElevation()
    }

    fun setShadowColorLight(@ColorInt shadowColor: Int) {
        shapeDrawable.setShadowColorLight(shadowColor)
    }

    fun setShadowColorDark(@ColorInt shadowColor: Int) {
        shapeDrawable.setShadowColorDark(shadowColor)
    }

    override fun setTranslationZ(translationZ: Float) {
        super.setTranslationZ(translationZ)
        if (isInitialized) {
            shapeDrawable.setTranslationZ(translationZ)
        }
    }

    companion object {
        private const val LOG_TAG = "NeumorphCardView"
    }
}
