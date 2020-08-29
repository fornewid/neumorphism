package soup.neumorphism.internal.util

import android.graphics.*
import android.graphics.drawable.Drawable


object BitmapUtils {

    fun Drawable.toBitmap(width: Int, height: Int): Bitmap {
        setBounds(0, 0, width, height)
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(output).let(::draw)
        return output
    }

    fun Bitmap.clipToRadius(radius: Float): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, width, height)
        val rectF = RectF(rect)

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(output).apply {
            drawARGB(0, 0, 0, 0)
            drawRoundRect(rectF, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmap(this@clipToRadius, rect, rect, paint)
        }

        return output
    }

}