package com.example.museraya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VintageFilmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VintageFilmFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vintage_film, container, false)

        // dito ilalagay yung id ng image
        val viewerImage: ImageView = view.findViewById(R.id.imageButton) // Replace with your actual ImageView ID
        val cameraImage: ImageView = view.findViewById(R.id.imageButton2) // Replace with your actual ImageView ID
        val polaroidImage: ImageView = view.findViewById(R.id.imageButton3) // Replace with your actual ImageView ID


        // dito ilalagay yung kung saan mapupunta pag pinindot mo yung nasa taas which is imageButton ang ID HAHAHAHA
        viewerImage.setOnClickListener {
            it.findNavController().navigate(R.id.filmViewerFragment)
        }
         cameraImage.setOnClickListener {
            it.findNavController().navigate(R.id.filmCameraFragment)
        }
        polaroidImage.setOnClickListener {
            it.findNavController().navigate(R.id.filmPolaroidFragment)
        }



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VintageFilmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VintageFilmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}