package soup.neumorphism

import android.content.Context
import android.graphics.Bitmap
import jp.wasabeef.blurry.internal.Blur
import jp.wasabeef.blurry.internal.BlurFactor

fun Bitmap.blurred(context: Context, radius: Int? = null, sampling: Int? = null): Bitmap {
    return Blur.of(context, this, BlurFactor().apply {
        this.width = this@blurred.width
        this.height = this@blurred.height
        if (radius != null) {
            this.radius = radius
        }
        if (sampling != null) {
            this.sampling = sampling
        }
    })
}
