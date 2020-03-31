package soup.neumorphism

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max

class NeumorphTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = R.style.defaultNeumorphTextView
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val shadowElevation: Float
    private val shadowColorLight: Int
    private val shadowColorDark: Int

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private var lastTextCache: Bitmap? = null
    private var lastShadowLight: Bitmap? = null
    private var lastShadowDark: Bitmap? = null

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NeumorphTextView, defStyleAttr, defStyleRes
        )
        shadowElevation = a.getDimensionPixelSize(
            R.styleable.NeumorphTextView_neumorph_shadowElevation, 0
        ).toFloat()
        shadowColorLight = a.getColor(
            R.styleable.NeumorphTextView_neumorph_shadowColorLight, Color.WHITE
        )
        shadowColorDark = a.getColor(
            R.styleable.NeumorphTextView_neumorph_shadowColorDark, Color.BLACK
        )
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        lastTextCache = buildTextCache(w, h).also { origin ->
            if (lastShadowLight == null) {
                lastShadowLight = origin.generateBitmapShadowCache(w, h, shadowColorLight)
            }
            if (lastShadowDark == null) {
                lastShadowDark = origin.generateBitmapShadowCache(w, h, shadowColorDark)
            }
        }
    }

    override fun draw(canvas: Canvas) {
        lastShadowLight?.let {
            canvas.drawBitmap(it, -shadowElevation, -shadowElevation, shadowPaint)
        }
        lastShadowDark?.let {
            canvas.drawBitmap(it, shadowElevation, shadowElevation, shadowPaint)
        }
        super.draw(canvas)
    }

    private fun buildTextCache(w: Int, h: Int): Bitmap {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            val tp = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
            tp.color = Color.BLACK
            tp.textSize = textSize
            tp.typeface = typeface
            staticLayout(text, tp).draw(Canvas(this))
        }
    }

    private fun staticLayout(text: CharSequence, textPaint: TextPaint): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, textPaint, Int.MAX_VALUE)
                .build()
        } else {
            StaticLayout(
                text, textPaint, Int.MAX_VALUE,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
            )
        }
    }

    private fun Bitmap.generateBitmapShadowCache(
        w: Int, h: Int, color: Int, radius: Float = 5f
    ): Bitmap? {
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Call mutate, so that the pixel allocation by the underlying vector drawable is cleared.
        canvas.save()
        canvas.drawBitmap(this, 0f, 0f, null)
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
}
