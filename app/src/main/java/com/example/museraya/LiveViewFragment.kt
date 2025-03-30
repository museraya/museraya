package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.museraya.adapters.ModelAdapter
import com.example.museraya.data.ModelItem

class LiveViewFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ModelAdapter

    private val models = listOf(
        ModelItem("Woodcutter's Nightmare", "Painting", R.id.woodcutterARFragment, R.drawable.woodcutter_nobg),
        ModelItem("Tex Reavis", "Painting", R.id.texArFragment, R.drawable.tex_nobg),
        ModelItem("Pronto B! Polaroid Camera", "Vintage Film", R.id.PolaroidFragmentAR, R.drawable.polaroid_nobg),
        ModelItem("Classic TV", "Vintage Film", R.id.filmViewerFragment, R.drawable.comingsoon),
        ModelItem("Retro Telephone", "Vintage Audio", R.id.audioBoomFragment, R.drawable.comingsoon),
        ModelItem("Gramophone", "Vintage Music", R.id.turntableFragment, R.drawable.comingsoon),
        ModelItem("Film Projector", "Vintage Film", R.id.filmPolaroidFragment, R.drawable.comingsoon)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liveview, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewModels)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columns
        adapter = ModelAdapter(models) { model -> navigateToModelFragment(model.fragmentId) }
        recyclerView.adapter = adapter
        return view
    }

    private fun navigateToModelFragment(fragmentId: Int) {
        findNavController().navigate(fragmentId)  // Navigate using NavController
    }
}
