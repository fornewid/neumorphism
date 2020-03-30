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
import soup.neumorphism.internal.withClipOut
import soup.neumorphism.internal.withTranslation

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

    private val outlinePath = Path()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NeumorphCardView)
        roundCornerRadius = a.getDimension(
            R.styleable.NeumorphCardView_neumorph_cornerSize,
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

        lightShadowDrawable.setSize(w, h)
        lightShadowDrawable.setBounds(0, 0, w, h)
        darkShadowDrawable.setSize(w, h)
        darkShadowDrawable.setBounds(0, 0, w, h)
        lastShadowCache = generateBitmapShadowCache(w, h)
    }

    override fun draw(canvas: Canvas) {
        canvas.withClipOut(outlinePath) {
            lastShadowCache?.let {
                val offset = (shadowElevation * 2).toFloat().unaryMinus()
                canvas.drawBitmap(it, offset, offset, null)
            }
        }
        canvas.withClip(outlinePath) {
            super.draw(this)
        }
    }

    private fun generateBitmapShadowCache(w: Int, h: Int): Bitmap? {
        val width: Int = w + shadowElevation * 4
        val height: Int = h + shadowElevation * 4
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
