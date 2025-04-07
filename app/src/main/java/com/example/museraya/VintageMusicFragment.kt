package com.example.museraya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VintageMusicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VintageMusicFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VintageMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vintage_music, container, false)

        recyclerView = view.findViewById(R.id.vintageMusicRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val items = listOf(
            VintageMusicItem(R.drawable.turntable, R.id.turntableFragment),
            VintageMusicItem(R.drawable.vinyl, R.id.vinylFragment),
            VintageMusicItem(R.drawable.single_vinyl, R.id.singleVinylFragment)
        )

        adapter = VintageMusicAdapter(items) { destinationId ->
            view.findNavController().navigate(destinationId)
        }

        recyclerView.adapter = adapter

        return view
    }
}

