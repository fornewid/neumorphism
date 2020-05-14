package soup.neumorphism

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import soup.neumorphism.internal.util.NeumorphResources

class NeumorphCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.neumorphCardViewStyle,
    defStyleRes: Int = R.style.Widget_Neumorph_CardView
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val shapeDrawable: NeumorphShapeDrawable

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NeumorphCardView, defStyleAttr, defStyleRes
        )
        val fillColor = a.getColorStateList(R.styleable.NeumorphCardView_neumorph_backgroundColor)
        val strokeColor = a.getColorStateList(R.styleable.NeumorphCardView_neumorph_strokeColor)
        val strokeWidth = a.getDimension(R.styleable.NeumorphCardView_neumorph_strokeWidth, 0f)
        val shapeType = a.getInt(R.styleable.NeumorphCardView_neumorph_shapeType, ShapeType.DEFAULT)
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
            setShapeType(shapeType)
            setShadowElevation(shadowElevation)
            setShadowColorLight(shadowColorLight)
            setShadowColorDark(shadowColorDark)
            setFillColor(fillColor)
            setStroke(strokeWidth, strokeColor)

            val left = paddingLeft
            val top = paddingTop
            val right = paddingRight
            val bottom = paddingBottom
            if (arrayOf(left, top, right, bottom).any { it > 0 }) {
                setPadding(left, top, right, bottom)
            }
        }
        setBackgroundInternal(shapeDrawable)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        shapeDrawable.setPadding(left, top, right, bottom)
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

    fun setShapeType(@ShapeType shapeType: Int) {
        shapeDrawable.setShapeType(shapeType)
    }

    @ShapeType
    fun getShapeType(): Int {
        return shapeDrawable.getShapeType()
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
        shapeDrawable.setTranslationZ(translationZ)
    }

    companion object {
        private const val LOG_TAG = "NeumorphCardView"
    }
}
