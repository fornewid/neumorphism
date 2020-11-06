package soup.neumorphism.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import soup.neumorphism.sample.databinding.ActivityMainBinding
import soup.neumorphism.sample.ui.CardSampleActivity
import soup.neumorphism.sample.ui.ColorSampleActivity
import soup.neumorphism.sample.ui.ListSampleActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textview.setOnClickListener {
            binding.textview.text = listOf("Neumorphism", "in", "Android").random()
        }

        binding.button.setOnClickListener {
            startActivity(Intent(this, ColorSampleActivity::class.java))
        }

        binding.flatCard.setOnClickListener {
            startActivity(Intent(this, CardSampleActivity::class.java))
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, ListSampleActivity::class.java))
        }
    }
}
