package soup.neumorphism

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import soup.neumorphism.internal.blurred
import soup.neumorphism.internal.withClip
import soup.neumorphism.internal.withTranslation

class NeumorphValleyView @JvmOverloads constructor(
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

    private val outlinePath = Path()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NeumorphValleyView)
        roundCornerRadius = a.getDimension(
            R.styleable.NeumorphValleyView_neumorph_cornerRadius,
            resources.getDimension(R.dimen.default_corner_radius)
        )
        shadowElevation = a.getDimensionPixelSize(
            R.styleable.NeumorphValleyView_neumorph_shadowElevation,
            resources.getDimensionPixelSize(R.dimen.default_shadow_elevation)
        )
        shadowColorLight = a.getColor(
            R.styleable.NeumorphValleyView_neumorph_shadowColorLight,
            ContextCompat.getColor(context, R.color.default_shadow_light)
        )
        shadowColorDark = a.getColor(
            R.styleable.NeumorphValleyView_neumorph_shadowColorDark,
            ContextCompat.getColor(context, R.color.default_shadow_dark)
        )
        a.recycle()

        lightShadowDrawable = GradientDrawable().apply {
            setSize(measuredWidth + shadowElevation, measuredHeight + shadowElevation)
            setStroke(shadowElevation, shadowColorLight)
            cornerRadii = roundCornerRadius.let {
                floatArrayOf(0f, 0f, 0f, 0f, it, it, 0f, 0f)
            }
        }
        darkShadowDrawable = GradientDrawable().apply {
            setSize(measuredWidth + shadowElevation, measuredHeight + shadowElevation)
            setStroke(shadowElevation, shadowColorDark)
            cornerRadii = roundCornerRadius.let {
                floatArrayOf(it, it, 0f, 0f, 0f, 0f, 0f, 0f)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        outlinePath.apply {
            reset()
            addRoundRect(
                0f, 0f, w.toFloat(), h.toFloat(),
                roundCornerRadius, roundCornerRadius,
                Path.Direction.CW
            )
            close()
        }
        val width = w + shadowElevation
        val height = h + shadowElevation
        lightShadowDrawable.setSize(width, height)
        lightShadowDrawable.setBounds(0, 0, width, height)
        darkShadowDrawable.setSize(width, height)
        darkShadowDrawable.setBounds(0, 0, width, height)

        lastShadowCache = generateBitmapShadowCache(w, h)
    }

    override fun draw(canvas: Canvas) {
        canvas.withClip(outlinePath) {
            lastShadowCache?.let {
                canvas.drawBitmap(it, 0f, 0f, null)
            }
            super.draw(this)
        }
    }

    private fun generateBitmapShadowCache(w: Int, h: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.withTranslation(-shadowElevation.toFloat(), -shadowElevation.toFloat()) {
            lightShadowDrawable.draw(this)
        }
        darkShadowDrawable.draw(canvas)
        return bitmap.blurred(context, radius = 25, sampling = 2)
    }
}
