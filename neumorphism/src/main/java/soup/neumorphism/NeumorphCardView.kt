package soup.neumorphism

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.withTranslation
import androidx.core.view.ViewCompat

class NeumorphCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val roundCornerRadius: Float
    private val shadowElevation: Int
    private val shadowColorLight: Int
    private val shadowColorDark: Int

    private var lastShadowCache: Bitmap? = null
    private val lightShadowDrawable: GradientDrawable
    private val darkShadowDrawable: GradientDrawable

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NeumorphCardView)
        roundCornerRadius = a.getDimension(
            R.styleable.NeumorphCardView_neumorph_cornerRadius,
            resources.getDimension(R.dimen.default_corner_radius)
        )
        shadowElevation = a.getDimensionPixelSize(
            R.styleable.NeumorphCardView_neumorph_shadowElevation,
            resources.getDimensionPixelSize(R.dimen.default_shadow_elevation)
        )
        shadowColorLight = a.getColor(
            R.styleable.NeumorphCardView_neumorph_shadowColorLight,
            ContextCompat.getColor(context, R.color.default_shadow_light)
        )
        shadowColorDark = a.getColor(
            R.styleable.NeumorphCardView_neumorph_shadowColorDark,
            ContextCompat.getColor(context, R.color.default_shadow_dark)
        )
        a.recycle()

        lightShadowDrawable = GradientDrawable().apply {
            setSize(measuredWidth, measuredHeight)
            cornerRadii = roundCornerRadius.let {
                floatArrayOf(it, it, it, it, it, it, it, it)
            }
            setColor(shadowColorLight)
        }
        darkShadowDrawable = GradientDrawable().apply {
            setSize(measuredWidth, measuredHeight)
            cornerRadii = roundCornerRadius.let {
                floatArrayOf(it, it, it, it, it, it, it, it)
            }
            setColor(shadowColorDark)
        }
    }

    override fun draw(canvas: Canvas) {
        if (ViewCompat.isLaidOut(this)) {
            val w = measuredWidth
            val h = measuredHeight
            lightShadowDrawable.setSize(w, h)
            lightShadowDrawable.setBounds(0, 0, w, h)
            darkShadowDrawable.setSize(w, h)
            darkShadowDrawable.setBounds(0, 0, w, h)

            lastShadowCache = generateBitmapShadowCache()
        }
        lastShadowCache?.let {
            val offset = (shadowElevation * 2).toFloat().unaryMinus()
            canvas.drawBitmap(it, offset, offset, null)
        }
        super.draw(canvas)
    }

    private fun generateBitmapShadowCache(): Bitmap? {
        val width: Int = measuredWidth + shadowElevation * 4
        val height: Int = measuredHeight + shadowElevation * 4
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.withTranslation(shadowElevation.toFloat(), shadowElevation.toFloat()) {
            lightShadowDrawable.draw(this)
        }
        canvas.withTranslation(shadowElevation.toFloat() * 3, shadowElevation.toFloat() * 3) {
            darkShadowDrawable.draw(this)
        }
        return bitmap.blurred(context)
    }
}
