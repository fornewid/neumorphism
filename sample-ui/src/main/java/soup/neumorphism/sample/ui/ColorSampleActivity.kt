package soup.neumorphism.sample.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import soup.neumorphism.sample.ui.databinding.ActivitySampleColorBinding

class ColorSampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleColorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleColorBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        onBackgroundColorChanged(Color.RED)
    }

    private fun onBackgroundColorChanged(backgroundColor: Int) {
        binding.run {
            card.setBackgroundColor(backgroundColor)
            imageView.setBackgroundColor(backgroundColor)
            imageButton.setBackgroundColor(backgroundColor)
            button.setBackgroundColor(backgroundColor)
            fab.setBackgroundColor(backgroundColor)
        }
    }

    private fun onBackgroundColorChanged(backgroundColor: ColorStateList?) {
        binding.run {
            card.setBackgroundColor(backgroundColor)
            imageView.setBackgroundColor(backgroundColor)
            imageButton.setBackgroundColor(backgroundColor)
            button.setBackgroundColor(backgroundColor)
            fab.setBackgroundColor(backgroundColor)
        }
    }

    private fun onStrokeColorChanged(strokeColor: ColorStateList?) {
        binding.run {
            card.setStrokeColor(strokeColor)
            imageView.setStrokeColor(strokeColor)
            imageButton.setStrokeColor(strokeColor)
            button.setStrokeColor(strokeColor)
            fab.setStrokeColor(strokeColor)
        }
    }

    private fun onStrokeWidthChanged(strokeWidth: Float) {
        binding.run {
            card.setStrokeWidth(strokeWidth)
            imageView.setStrokeWidth(strokeWidth)
            imageButton.setStrokeWidth(strokeWidth)
            button.setStrokeWidth(strokeWidth)
            fab.setStrokeWidth(strokeWidth)
        }
    }
}
