package soup.neumorphism.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import soup.neumorphism.sample.ui.databinding.ActivitySampleListBinding
import soup.neumorphism.sample.ui.databinding.ItemCardBinding

class ListSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySampleListBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = Adapter(data = (0..50).map { "Android" })
        }
    }

    class Adapter(private val data: List<String>) : RecyclerView.Adapter<CardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            return CardViewHolder(
                ItemCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.binding.textview.text = data[position]
        }

        override fun getItemCount(): Int = data.size
    }

    class CardViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)
}
