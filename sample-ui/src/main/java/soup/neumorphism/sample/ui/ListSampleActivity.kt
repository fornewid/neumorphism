package soup.neumorphism.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.neumorphism.sample.ui.databinding.ActivitySampleListBinding
import soup.neumorphism.sample.ui.databinding.ItemCardBinding

class ListSampleActivity : AppCompatActivity() {

    val adapter = Adapter().apply { submitList((0..50).map { "Android" }) }
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySampleListBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        binding.recyclerView.adapter = adapter
    }

    class Adapter : ListAdapter<String, Holder>(StringDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder.create(parent)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    class Holder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(string: String) {
            binding.textview.text = string
        }

        companion object {
            fun create(parent: ViewGroup): Holder {
                val inflater = LayoutInflater.from(parent.context)
                return Holder(ItemCardBinding.inflate(inflater, parent, false))
            }
        }
    }

    private object StringDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
