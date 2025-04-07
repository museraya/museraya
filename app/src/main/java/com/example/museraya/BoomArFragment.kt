package com.example.museraya

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
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

class BoomArFragment : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null
    private var modelNode: ModelNode? = null // Reference to the model node
    private var isModelLocked = false // Tracks whether the model is locked in place

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_boom_ar, container, false)
        arSceneView = view.findViewById(R.id.arSceneViewWoodCutter)
        // btnPlaceModel padin yung id di ko na pinalitan
        placeModelButton = view.findViewById(R.id.btnPlaceModelWoodCutter)

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

            // Load HDR environment
            environment = environmentLoader.createHDREnvironment(
                assetFileLocation = "environments/HDR_040_Field_Env.hdr"
            )!!

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
            toggleModelLockState()
        }
    }

    private fun placeAnchor(anchor: Anchor) {
        if (anchorNode == null) {
            anchorNode = AnchorNode(arSceneView.engine, anchor).apply {
                isEditable = true // Initially allow movement sa unang place
                lifecycleScope.launch { //dito na magstastart yung AR lifecycle
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter/boommicrophone.glb"
                    )
                    if (modelInstance != null) {
                        Toast.makeText(requireContext(), "Model loaded successfully", Toast.LENGTH_SHORT).show()

                        // Create the main model node and initial sizes and measurements
                        modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            centerOrigin = Position(y = -0.5f)
                        ).apply { isEditable = true } // Initially editable

                        addChildNode(modelNode!!)
                    } else {
                        Toast.makeText(requireContext(), "Failed to load model", Toast.LENGTH_SHORT).show()
                    }
                }

                arSceneView.addChildNode(this)
            }
        }
    }

    private fun toggleModelLockState() {
        if (anchorNode != null && modelNode != null) {
            isModelLocked = !isModelLocked // Toggle the lock state

            if (isModelLocked) {
                // bawal galawin yung size and rotation bruh
                anchorNode?.isEditable = false
                modelNode?.isEditable = false
                Toast.makeText(requireContext(), "Model rotation and size locked!", Toast.LENGTH_SHORT).show()
            } else {
                // pwede magalaw yung rotation and size bruh
                anchorNode?.isEditable = true
                modelNode?.isEditable = true
                Toast.makeText(requireContext(), "Model rotation and size unlocked!", Toast.LENGTH_SHORT).show()
            }

            // update ng text button bruh
            placeModelButton.text = if (isModelLocked) "Unlock Rotation" else "lock Rotation"
        } else {
            Toast.makeText(requireContext(), "No model to lock!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        arSceneView.destroy()
    }
}
