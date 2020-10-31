package soup.neumorphism.sample.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import soup.neumorphism.NeumorphTextView
import soup.neumorphism.sample.R
import soup.neumorphism.sample.collection.MyAdapter.MyViewHolder

class MyAdapter(private val mDataset: List<String>) : RecyclerView.Adapter<MyViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(  // each data item is just a string in this case
        var myView: View
    ) : ViewHolder(myView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_viewholder, parent, false)
        return MyViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val nTextView: NeumorphTextView = holder.myView.findViewById(R.id.textview)
        nTextView.text = mDataset[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataset.size
    }
}