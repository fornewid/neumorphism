package soup.neumorphism

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.drawToBitmap
import jp.wasabeef.blurry.Blurry

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.card_light_shadow).run {
            doOnPreDraw {
                blurred()
            }
        }
        findViewById<ImageView>(R.id.card_dark_shadow).run {
            doOnPreDraw {
                blurred()
            }
        }

        findViewById<View>(R.id.hole_button).run {
            setClipToRoundRect(resources.getDimension(R.dimen.corner_radius))
        }
        findViewById<ImageView>(R.id.hole_light_shadow).run {
            doOnPreDraw {
                blurred(sampling = 2)
            }
        }
        findViewById<ImageView>(R.id.hole_dark_shadow).run {
            doOnPreDraw {
                blurred(sampling = 2)
            }
        }
    }

    private fun ImageView.blurred(radius: Int? = null, sampling: Int? = null) {
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
}
