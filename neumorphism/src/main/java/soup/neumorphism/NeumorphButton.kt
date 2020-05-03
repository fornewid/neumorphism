package soup.neumorphism

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton

class NeumorphButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.neumorphButtonStyle,
    defStyleRes: Int = R.style.Widget_Neumorph_Button
) : AppCompatButton(context, attrs, defStyleAttr) {

    private val shapeDrawable: NeumorphShapeDrawable

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NeumorphButton, defStyleAttr, defStyleRes
        )
        val shapeType = a.getInt(R.styleable.NeumorphButton_neumorph_shapeType, ShapeType.DEFAULT)
        val shadowElevation = a.getDimension(
            R.styleable.NeumorphButton_neumorph_shadowElevation, 0f
        )
        val shadowColorLight = a.getColor(
            R.styleable.NeumorphButton_neumorph_shadowColorLight, Color.WHITE
        )
        val shadowColorDark = a.getColor(
            R.styleable.NeumorphButton_neumorph_shadowColorDark, Color.BLACK
        )
        a.recycle()

        shapeDrawable = NeumorphShapeDrawable(context, attrs, defStyleAttr, defStyleRes).apply {
            setShapeType(shapeType)
            setShadowElevation(shadowElevation)
            setShadowColorLight(shadowColorLight)
            setShadowColorDark(shadowColorDark)
        }
        setBackgroundInternal(shapeDrawable)
    }

    override fun setBackground(drawable: Drawable?) {
        setBackgroundDrawable(drawable)
    }

    override fun setBackgroundDrawable(drawable: Drawable?) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN, MotionEvent.ACTION_BUTTON_PRESS -> shapeDrawable.setShapeType(ShapeType.DEFAULT) // Pressed down
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_BUTTON_RELEASE -> shapeDrawable.setShapeType(ShapeType.PRESSED)
            else -> shapeDrawable.setShapeType(ShapeType.PRESSED)
        }
        return super.onTouchEvent(event)
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

    companion object {
        private const val LOG_TAG = "NeumorphButton"
    }
}
