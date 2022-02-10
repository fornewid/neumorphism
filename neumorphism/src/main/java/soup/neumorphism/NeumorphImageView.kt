package soup.neumorphism

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import soup.neumorphism.internal.util.NeumorphResources
import kotlin.math.roundToInt

open class NeumorphImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.neumorphImageViewStyle,
    defStyleRes: Int = R.style.Widget_Neumorph_ImageView
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isInitialized: Boolean = false
    private val shapeDrawable: NeumorphShapeDrawable

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NeumorphImageView, defStyleAttr, defStyleRes
        )

        val backgroundDrawable = a.getDrawable(R.styleable.NeumorphImageButton_neumorph_backgroundDrawable)
        val fillColor = a.getColorStateList(R.styleable.NeumorphImageView_neumorph_backgroundColor)
        val strokeColor = a.getColorStateList(R.styleable.NeumorphImageView_neumorph_strokeColor)
        val strokeWidth = a.getDimension(R.styleable.NeumorphImageView_neumorph_strokeWidth, 0f)
        val shapeType = a.getInt(R.styleable.NeumorphImageView_neumorph_shapeType, ShapeType.FLAT.ordinal)
            .let { ordinal ->
                ShapeType.values()[ordinal]
            }

        val shadowElevation = a.getDimension(
            R.styleable.NeumorphImageView_neumorph_shadowElevation, 0f
        ).roundToInt()

        val shadowRadius = a.getDimension(
            R.styleable.NeumorphImageView_neumorph_shadowRadius, 15f
        ).roundToInt()

        val shadowColorLight = NeumorphResources.getColor(
            context, a,
            R.styleable.NeumorphImageView_neumorph_shadowColorLight,
            R.color.design_default_color_shadow_light
        )
        val shadowColorDark = NeumorphResources.getColor(
            context, a,
            R.styleable.NeumorphImageView_neumorph_shadowColorDark,
            R.color.design_default_color_shadow_dark
        )
        a.recycle()

        shapeDrawable = NeumorphShapeDrawable(context, attrs, defStyleAttr, defStyleRes).apply {
            setInEditMode(isInEditMode)
            setShapeType(shapeType)
            setShadowElevation(shadowElevation)
            setShadowRadius(shadowRadius)
            setShadowColorLight(shadowColorLight)
            setShadowColorDark(shadowColorDark)
            setBackgroundDrawable(backgroundDrawable)
            setFillColor(fillColor)
            setStroke(strokeWidth, strokeColor)
            setTranslationZ(translationZ)
        }

        setBackgroundInternal(shapeDrawable)
        isInitialized = true
    }

    override fun setBackground(drawable: Drawable?) {
        setBackgroundDrawable(drawable)
    }

    override fun setBackgroundDrawable(drawable: Drawable?) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.")
    }

    fun setNeumorphBackgroundDrawable(drawable: Drawable?) {
        shapeDrawable.setBackgroundDrawable(drawable)
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

    fun setShapeType(shapeType: ShapeType) {
        shapeDrawable.setShapeType(shapeType)
    }

    fun getShapeType(): ShapeType {
        return shapeDrawable.getShapeType()
    }

    fun setShadowElevation(shadowElevation: Int) {
        shapeDrawable.setShadowElevation(shadowElevation)
    }

    fun getNeumorphShadowRadius(): Int {
        return shapeDrawable.getShadowElevation()
    }

    fun setNeumorphShadowRadius(shadowRadius: Int) {
        shapeDrawable.setShadowRadius(shadowRadius)
    }

    fun getShadowRadius(): Int {
        return shapeDrawable.getShadowRadius()
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

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        shapeDrawable.setBackgroundDrawableState(drawableState)
    }

    override fun refreshDrawableState() {
        super.refreshDrawableState()
        shapeDrawable.setBackgroundDrawableState(drawableState)
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        shapeDrawable.setBackgroundDrawableState(drawableState)
    }

    companion object {
        private const val LOG_TAG = "NeumorphImageView"
    }
}
