package soup.neumorphism

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import kotlin.math.max

class NeumorphTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val shadowElevation: Float
    private val shadowColorLight: Int
    private val shadowColorDark: Int

    private var lastTextCache: Bitmap? = null
    private var lastShadowLight: Bitmap? = null
    private var lastShadowDark: Bitmap? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NeumorphTextView)
        shadowElevation = a.getDimension(R.styleable.NeumorphTextView_neumorph_shadowElevation, 5f)
        shadowColorLight = a.getColor(
            R.styleable.NeumorphTextView_neumorph_shadowColorLight,
            ContextCompat.getColor(context, R.color.default_shadow_light)
        )
        shadowColorDark = a.getColor(
            R.styleable.NeumorphTextView_neumorph_shadowColorDark,
            ContextCompat.getColor(context, R.color.default_shadow_dark)
        )
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        if (ViewCompat.isLaidOut(this)) {
            lastTextCache = buildTextCache().also { origin ->
                if (lastShadowLight == null) {
                    lastShadowLight = generateBitmapShadowCache(origin, shadowColorLight)
                }
                if (lastShadowDark == null) {
                    lastShadowDark = generateBitmapShadowCache(origin, shadowColorDark)
                }
            }
            lastShadowLight?.let {
                canvas.drawBitmap(it, -shadowElevation, -shadowElevation, shadowPaint)
            }
            lastShadowDark?.let {
                canvas.drawBitmap(it, shadowElevation, shadowElevation, shadowPaint)
            }
        }
        super.onDraw(canvas)
    }

    private fun buildTextCache(): Bitmap {
        return Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888).apply {
            val tp = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
            tp.color = Color.BLACK
            tp.textSize = textSize
            tp.typeface = typeface
            staticLayout(text, tp).draw(Canvas(this))
        }
    }

    private fun generateBitmapShadowCache(cache: Bitmap, color: Int, radius: Float = 5f): Bitmap? {
        val width: Int = measuredWidth
        val height: Int = measuredHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Call mutate, so that the pixel allocation by the underlying vector drawable is cleared.
        canvas.save()
        canvas.drawBitmap(cache, 0f, 0f, null)
        canvas.restore()

        // Draws the shadow from original drawable
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.color = color
        paint.maskFilter = BlurMaskFilter(max(1f, radius), BlurMaskFilter.Blur.NORMAL)
        val offset = IntArray(2)
        val shadow = bitmap.extractAlpha(paint, offset)
        paint.maskFilter = null
        bitmap.eraseColor(Color.TRANSPARENT)
        canvas.drawBitmap(shadow, offset[0].toFloat(), offset[1].toFloat(), paint)
        return bitmap
    }

    private fun staticLayout(text: CharSequence, textPaint: TextPaint): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, textPaint, Int.MAX_VALUE)
                .build()
        } else {
            StaticLayout(
                text, textPaint, Int.MAX_VALUE,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
            )
        }
    }
}
