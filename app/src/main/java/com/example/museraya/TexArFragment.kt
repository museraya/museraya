package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView // Import TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState // Import TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Job // Import Job
import kotlinx.coroutines.delay // Import delay
import kotlinx.coroutines.launch

class TexArFragment : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var tvInstructions: TextView // Add TextView reference
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null
    private var modelNode: ModelNode? = null
    private var isModelLocked = false

    // Coroutine job for hiding interaction instructions
    private var interactionInstructionJob: Job? = null

    // Flag to track if the model has been placed at least once
    private var modelHasBeenPlaced = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tex_ar, container, false)
        arSceneView = view.findViewById(R.id.arSceneView)
        placeModelButton = view.findViewById(R.id.btnPlaceModel)
        tvInstructions = view.findViewById(R.id.tvInstructionsTex) // Get reference to TextView using its ID

        materialLoader = MaterialLoader(arSceneView.engine, requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show initial scanning instruction
        showScanningInstructions()

        arSceneView.apply {
            lifecycle = viewLifecycleOwner.lifecycle
            planeRenderer.isEnabled = true
            planeRenderer.isShadowReceiver = false

            environment = environmentLoader.createHDREnvironment(
                assetFileLocation = "environments/HDR_040_Field_Env.hdr"
            )!!

            configureSession { _, config ->
                config.depthMode = Config.DepthMode.AUTOMATIC
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            onSessionUpdated = { session, frame ->
                // Check if ARCore is tracking
                if (frame.camera.trackingState == TrackingState.TRACKING) {
                    // Check if a model has been placed yet. If not, keep showing scanning instructions.
                    if (!modelHasBeenPlaced) {
                        showScanningInstructions() // Keep showing if no model placed yet

                        // Try to find a plane and place the anchor
                        frame.getUpdatedPlanes().firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { plane ->
                                if (anchorNode == null) { // Place only once initially
                                    placeAnchor(plane.createAnchor(plane.centerPose))
                                }
                            }
                    }
                    // If model is placed, instructions are handled elsewhere
                } else {
                    // Optional: Handle tracking loss indication
                    // showScanningInstructions() // Or show a specific "Tracking Lost" message
                }
            }
        }

        placeModelButton.setOnClickListener {
            toggleModelLockState()
        }
    }

    private fun showScanningInstructions() {
        // Cancel any pending hide operations for interaction instructions
        interactionInstructionJob?.cancel()
        tvInstructions.text = "Find a well-lit surface.\nSlowly move device to scan."
        tvInstructions.visibility = View.VISIBLE
    }

    private fun showInteractionInstructions() {
        // Cancel any pending hide job before starting a new one
        interactionInstructionJob?.cancel()

        tvInstructions.text = "Pinch to resize\nTwist to rotate"
        tvInstructions.visibility = View.VISIBLE

        // Launch a coroutine to hide the instructions after 5 seconds
        interactionInstructionJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(10000) // 10 seconds delay
            tvInstructions.visibility = View.GONE // Hide the text view
        }
    }

    private fun placeAnchor(anchor: Anchor) {
        if (anchorNode == null) { // Ensure we only place the anchor and model once automatically
            anchorNode = AnchorNode(arSceneView.engine, anchor).apply {
                isEditable = !isModelLocked // Sync with lock state initially

                lifecycleScope.launch {
                    // Make sure the path is correct for this fragment's model
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter/tex.glb" // Ensure this path is correct
                    )
                    if (modelInstance != null) {
                        modelHasBeenPlaced = true // Mark that the model is now placed
                        Toast.makeText(requireContext(), "Model loaded successfully", Toast.LENGTH_SHORT).show()

                        modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            centerOrigin = Position(y = -0.5f)
                        ).apply {
                            isEditable = !isModelLocked // Sync with lock state
                        }

                        addChildNode(modelNode!!)

                        // Model is placed, show interaction instructions
                        showInteractionInstructions()

                    } else {
                        Toast.makeText(requireContext(), "Failed to load model", Toast.LENGTH_SHORT).show()
                        // Reset to allow retrying placement
                        anchorNode?.let { arSceneView.removeChildNode(it) } // Remove the failed anchor node
                        anchorNode = null
                        modelHasBeenPlaced = false // Reset flag
                        showScanningInstructions() // Revert to scanning instructions
                    }
                }
                arSceneView.addChildNode(this) // Add anchor node regardless of model load success initially
                // It gets removed above if model loading fails.
            }
        }
    }


    private fun toggleModelLockState() {
        if (anchorNode != null && modelNode != null) {
            isModelLocked = !isModelLocked

            anchorNode?.isEditable = !isModelLocked // Update editability based on lock state
            modelNode?.isEditable = !isModelLocked // Update editability based on lock state

            val message = if (isModelLocked) "Model interaction locked!" else "Model interaction unlocked!"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            // Update button text
            placeModelButton.text = if (isModelLocked) "Unlock Interaction" else "Lock Interaction"

        } else {
            Toast.makeText(requireContext(), "Place the model first!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel any ongoing coroutine job
        interactionInstructionJob?.cancel()
        arSceneView.destroy()
    }
}