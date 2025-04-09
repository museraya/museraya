package com.example.museraya

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class woodcutter : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var tvInstructions: TextView // Add TextView reference
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null
    private var modelNode: ModelNode? = null
    private var isModelLocked = false

    // Handler and Runnable for instruction visibility delay
    private val instructionHandler = Handler(Looper.getMainLooper())
    private var hideInstructionRunnable: Runnable? = null
    private var interactionInstructionJob: Job? = null // Coroutine job for hiding interaction instructions

    // Flag to track if the model has been placed at least once
    private var modelHasBeenPlaced = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_woodcutterar, container, false)
        arSceneView = view.findViewById(R.id.arSceneViewWoodCutter)
        placeModelButton = view.findViewById(R.id.btnPlaceModelWoodCutter)
        tvInstructions = view.findViewById(R.id.tvInstructions) // Get reference to TextView

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
                    } else {
                        // If model is placed, instructions are handled by placeAnchor/showInteractionInstructions
                        // You could potentially hide instructions here if needed after tracking is regained,
                        // but the 5-second delay handles the main interaction case.
                    }
                } else {
                    // Optional: Show a message if tracking is lost
                    // tvInstructions.text = "Tracking lost. Move device slowly."
                    // tvInstructions.visibility = View.VISIBLE
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
                // Keep isEditable = true initially if you want the user
                // to potentially reposition slightly right after placement,
                // before the interaction instructions appear.
                // If not, you can set it to false here if the lock button controls all interaction.
                isEditable = !isModelLocked // Sync with lock state

                lifecycleScope.launch {
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter/frame.gltf"
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
                        // If model fails to load, maybe reset anchorNode to allow trying again?
                        anchorNode = null // Allow re-detection and placement attempt
                        showScanningInstructions() // Revert to scanning instructions
                    }
                }
                arSceneView.addChildNode(this)
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

            // Update button text (Consider changing "Rotation" to "Interaction" for clarity)
            placeModelButton.text = if (isModelLocked) "Unlock Interaction" else "Lock Interaction"

            // If unlocking, maybe show interaction instructions again briefly? Optional.
            // if (!isModelLocked) {
            //     showInteractionInstructions()
            // } else {
            //      interactionInstructionJob?.cancel() // Cancel hide job if locking
            //      tvInstructions.visibility = View.GONE // Hide immediately when locked
            // }

        } else {
            Toast.makeText(requireContext(), "Place the model first!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel any ongoing coroutine job to avoid leaks
        interactionInstructionJob?.cancel()
        arSceneView.destroy() // SceneView destruction is important
    }
}