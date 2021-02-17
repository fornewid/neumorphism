package soup.neumorphism.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import soup.neumorphism.sample.ui.databinding.ActivityBottomNavBinding

class BottomNavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.bottomNav.setOnMenuItemSelectedListener { id ->
            val text = when(id) {
                R.id.home -> "home"
                R.id.activity -> "activity"
                R.id.favorites -> "favorites"
                R.id.settings -> "settings"
                else -> "something went wrong"
            }
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
}