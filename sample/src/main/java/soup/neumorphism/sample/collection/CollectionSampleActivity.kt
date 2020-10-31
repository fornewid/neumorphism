package soup.neumorphism.sample.collection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import soup.neumorphism.sample.R

class CollectionSampleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val dataSet = listOf(
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
        "Android",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_sample)

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(dataSet)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

}
