package soup.neumorphism.sample

import android.widget.ImageView
import androidx.core.view.drawToBitmap
import jp.wasabeef.blurry.Blurry

fun ImageView.blurred(radius: Int? = null, sampling: Int? = null) {
    Blurry.with(context)
        .apply {
            if (radius != null) {
                radius(radius)
            }
            if (sampling != null) {
                sampling(sampling)
            }
        }
        .from(drawToBitmap())
        .into(this)
}
