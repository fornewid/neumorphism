package soup.neumorphism.internal.util

import android.graphics.*
import android.graphics.drawable.Drawable

object BitmapUtils {

    fun Drawable.clipToPath(rect: RectF): Bitmap {
        val output = Bitmap.createBitmap(rect.width().toInt(), rect.height().toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
        return output
    }

    fun Bitmap.clipToRadius(radius: Float): Bitmap {
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(this, rect, rect, paint)
        return output
    }

}