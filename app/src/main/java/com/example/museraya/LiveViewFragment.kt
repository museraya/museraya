package com.example.museraya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch


class LiveViewFragment : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private var anchorNode: AnchorNode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liveview, container, false)
        arSceneView = view.findViewById(R.id.arSceneView)
        placeModelButton = view.findViewById(R.id.btnPlaceModel)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arSceneView.apply {
            lifecycle = viewLifecycleOwner.lifecycle
            planeRenderer.isEnabled = true

            configureSession { session, config ->
                config.depthMode = Config.DepthMode.AUTOMATIC
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            onSessionUpdated = { _, frame ->
                frame.getUpdatedPlanes().firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    ?.let { plane ->
                        anchorNode ?: run {
                            placeAnchor(plane.createAnchor(plane.centerPose))
                        }
                    }
            }
        }

        placeModelButton.setOnClickListener {
            // Use the latest frame from the onSessionUpdated callback
            arSceneView.onSessionUpdated = { _, frame ->
                // Explicitly define the type of the parameter as Plane
                val plane: Plane? = frame.getUpdatedPlanes()
                    .firstOrNull { detectedPlane -> detectedPlane.type == Plane.Type.HORIZONTAL_UPWARD_FACING }

                // If a plane is found, create an anchor and place the model
                plane?.let { detectedPlane ->
                    placeAnchor(detectedPlane.createAnchor(detectedPlane.centerPose))
                }
            }
        }


    }

    private fun placeAnchor(anchor: Anchor) {
        if (anchorNode == null) {
            anchorNode = AnchorNode(arSceneView.engine, anchor).apply {
                isEditable = true
                lifecycleScope.launch {
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter.glb"
                    )
                    if (modelInstance != null) {
                        Toast.makeText(requireContext(), "Model loaded successfully", Toast.LENGTH_SHORT).show()
                        addChildNode(
                            ModelNode(
                                modelInstance = modelInstance,
                                scaleToUnits = 0.5f,
                                centerOrigin = Position(y = -0.5f)
                            ).apply { isEditable = true }
                        )
                    } else {
                        Toast.makeText(requireContext(), "Failed to load model", Toast.LENGTH_SHORT).show()
                    }
                }

                arSceneView.addChildNode(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        arSceneView.destroy()
    }
}