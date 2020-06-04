package soup.neumorphism.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            startActivity(Intent(this, ColorSampleActivity::class.java))
        }

        findViewById<View>(R.id.flat_card).setOnClickListener {
            startActivity(Intent(this, CardSampleActivity::class.java))
        }
    }
}
