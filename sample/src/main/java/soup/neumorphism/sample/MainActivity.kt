package soup.neumorphism.sample

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.fab_light_shadow).run {
            doOnPreDraw {
                blurred()
            }
        }
        findViewById<ImageView>(R.id.fab_dark_shadow).run {
            doOnPreDraw {
                blurred()
            }
        }
    }
}
