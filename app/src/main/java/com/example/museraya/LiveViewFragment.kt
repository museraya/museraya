package com.example.museraya

import android.graphics.Color
import android.os.Bundle
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
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch
import com.google.android.filament.utils.Float4

class LiveViewFragment : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liveview, container, false)
        arSceneView = view.findViewById(R.id.arSceneView)
        placeModelButton = view.findViewById(R.id.btnPlaceModel)

        // Initialize MaterialLoader with ARSceneView's engine
        materialLoader = MaterialLoader(arSceneView.engine, requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arSceneView.apply {
            lifecycle = viewLifecycleOwner.lifecycle
            planeRenderer.isEnabled = true
            planeRenderer.isShadowReceiver = false

            configureSession { _, config ->
                config.depthMode = Config.DepthMode.AUTOMATIC
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            onSessionUpdated = { _, frame ->
                frame.getUpdatedPlanes().firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    ?.let { plane ->
                        if (anchorNode == null) {
                            placeAnchor(plane.createAnchor(plane.centerPose))
                        }
                    }
            }
        }

        placeModelButton.setOnClickListener {
            arSceneView.onSessionUpdated = { _, frame ->
                frame.getUpdatedPlanes()
                    .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    ?.let { plane ->
                        placeAnchor(plane.createAnchor(plane.centerPose))
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
                        "https://sceneview.github.io/assets/models/DamagedHelmet.glb"
                    )
                    if (modelInstance != null) {
                        Toast.makeText(requireContext(), "Model loaded successfully", Toast.LENGTH_SHORT).show()

                        // Create the main model node
                        val modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            centerOrigin = Position(y = -0.5f)
                        ).apply { isEditable = true }

                        // Modify the material instance to set a default color
                        modelInstance.materialInstances.forEach { materialInstance ->
                            try {
                                // Use a float array for RGBA values
                                materialInstance.setParameter(
                                    "baseColorFactor",
                                    1.0f, 1.0f, 0.0f, 1.0f // Yellow color
                                )
                            } catch (e: IllegalArgumentException) {
                                // Log an error if the parameter is not supported
                                e.printStackTrace()
                                Toast.makeText(requireContext(), "Failed to set material color", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // Add a bounding box for visual feedback
                        val boundingBoxNode = CubeNode(
                            arSceneView.engine,
                            size = modelNode.extents,
                            center = modelNode.center,
                            materialInstance = materialLoader.createColorInstance(
                                Color.argb(51, 0, 255, 0) // Semi-transparent green
                            )
                        ).apply {
                            isVisible = true // Make the bounding box visible
                        }

                        modelNode.addChildNode(boundingBoxNode)
                        addChildNode(modelNode)
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
