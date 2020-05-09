package soup.neumorphism

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatEditText
import soup.neumorphism.internal.blur.BlurProvider
import soup.neumorphism.internal.util.NeumorphResources
import soup.neumorphism.internal.util.withClip
import soup.neumorphism.internal.util.withTranslation

class NeumorphEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.neumorphEditTextStyle,
    defStyleRes: Int = R.style.Widget_Neumorph_EditText
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val shapeDrawable: NeumorphShapeDrawable
    private val underlineDrawable: NeumorphShapeDrawable

    private val underlineHeight: Int
    private val underlineInsetBottom: Int
    private val underlineCornerSize: Float
    private val underlineElevation: Float

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NeumorphEditText, defStyleAttr, defStyleRes
        )
        val fillColor = a.getColorStateList(R.styleable.NeumorphEditText_neumorph_backgroundColor)
        val strokeColor = a.getColorStateList(R.styleable.NeumorphEditText_neumorph_strokeColor)
        val strokeWidth = a.getDimension(R.styleable.NeumorphEditText_neumorph_strokeWidth, 0f)
        val shapeType = a.getInt(R.styleable.NeumorphEditText_neumorph_shapeType, ShapeType.DEFAULT)
        val shadowElevation = a.getDimension(
            R.styleable.NeumorphEditText_neumorph_shadowElevation, 0f
        )
        val shadowColorLight = NeumorphResources.getColor(
            context, a,
            R.styleable.NeumorphEditText_neumorph_shadowColorLight,
            R.color.design_default_color_shadow_light
        )
        val shadowColorDark = NeumorphResources.getColor(
            context, a,
            R.styleable.NeumorphEditText_neumorph_shadowColorDark,
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
        }
        with(context.resources) {
            underlineHeight = getDimensionPixelSize(R.dimen.edit_text_underline_height)
            underlineInsetBottom = getDimensionPixelSize(R.dimen.edit_text_underline_inset_bottom)
            underlineCornerSize = getDimension(R.dimen.edit_text_underline_corner_size)
            underlineElevation = getDimension(R.dimen.edit_text_underline_elevation)
        }
        underlineDrawable = NeumorphShapeDrawable(
            NeumorphShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, underlineCornerSize)
                .build(),
            BlurProvider(context)
        ).apply {
            setShapeType(ShapeType.PRESSED)
            setShadowElevation(underlineElevation)
            setShadowColorLight(shadowColorLight)
            setShadowColorDark(shadowColorDark)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shapeDrawable.setBounds(0, 0, w, h)
        underlineDrawable.setBounds(0, 0, w - paddingLeft - paddingRight, underlineHeight)
    }

    override fun draw(canvas: Canvas) {
        shapeDrawable.draw(canvas)
        canvas.withClip(shapeDrawable.getOutlinePath()) {
            super.draw(this)
        }
        canvas.withTranslation(
            x = paddingLeft.toFloat(),
            y = (height - underlineHeight - underlineInsetBottom).toFloat()
        ) {
            underlineDrawable.draw(canvas)
        }
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
        private const val LOG_TAG = "NeumorphEditText"
    }
}
