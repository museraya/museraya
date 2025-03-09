package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Model Data Class
data class ModelItem(val name: String, val category: String, val fragment: Fragment, val thumbnailResId: Int)

// RecyclerView Adapter
class ModelAdapter(
    private val models: List<ModelItem>,
    private val onModelSelected: (ModelItem) -> Unit
) : RecyclerView.Adapter<ModelAdapter.ModelViewHolder>() {

    inner class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.modelName)
        val category: TextView = itemView.findViewById(R.id.modelCategory)
        val thumbnail: ImageView = itemView.findViewById(R.id.modelThumbnail)
        val selectButton: Button = itemView.findViewById(R.id.selectModelButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = models[position]
        holder.name.text = model.name
        holder.category.text = model.category
        holder.thumbnail.setImageResource(model.thumbnailResId)
        holder.selectButton.setOnClickListener { onModelSelected(model) }
    }

    override fun getItemCount() = models.size
}

// Live View Fragment (Displays catalog)
class LiveViewFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ModelAdapter

    private val models = listOf(
        ModelItem("Woodcutter", "Vintage Artifacts", WoodcutterFragment(), R.drawable.snorlax),
        ModelItem("Vintage Radio", "Vintage Audio", VintageRadioFragment(), R.drawable.snorlax),
        ModelItem("Old Camera", "Vintage Film", WoodcutterFragment(), R.drawable.snorlax),
        ModelItem("Classic TV", "Vintage Film", WoodcutterFragment(), R.drawable.snorlax),
        ModelItem("Retro Telephone", "Vintage Audio", WoodcutterFragment(), R.drawable.snorlax),
        ModelItem("Gramophone", "Vintage Music", WoodcutterFragment(), R.drawable.snorlax),
        ModelItem("Film Projector", "Vintage Film", WoodcutterFragment(), R.drawable.snorlax)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liveview, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewModels)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columns
        adapter = ModelAdapter(models) { model -> openARFragment(model.fragment) }
        recyclerView.adapter = adapter
        return view
    }

    private fun openARFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment) // Make sure this is the correct container ID
            .addToBackStack(null)
            .commit()
    }
}
