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
import androidx.lifecycle.Lifecycle // Import Lifecycle for state check

class TexArFragment : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var tvInstructions: TextView // Instruction TextView reference
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null
    private var modelNode: ModelNode? = null
    private var isModelLocked = false

    // Coroutine job for hiding interaction instructions
    private var interactionInstructionJob: Job? = null

    // Flag to track if the model has been placed at least once
    private var modelHasBeenPlaced = false

    // --- NEW: Variables for Plane Renderer Timer ---
    private var planeRendererTimerJob: Job? = null
    private var planeRendererTimerStarted = false
    // --- END NEW ---

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // !! IMPORTANT !! Ensure this layout and the IDs below are correct for TexArFragment
        val view = inflater.inflate(R.layout.fragment_tex_ar, container, false)

        // IDs should match fragment_tex_ar.xml (or your actual layout)
        arSceneView = view.findViewById(R.id.arSceneView) // Verify ID
        placeModelButton = view.findViewById(R.id.btnPlaceModel) // Verify ID
        tvInstructions = view.findViewById(R.id.tvInstructionsTex) // Verify ID

        materialLoader = MaterialLoader(arSceneView.engine, requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- NEW: Reset timer flag on view creation ---
        planeRendererTimerStarted = false
        // --- END NEW ---

        showScanningInstructions()

        arSceneView.apply {
            lifecycle = viewLifecycleOwner.lifecycle
            planeRenderer.isEnabled = true // Ensure it starts enabled
            planeRenderer.isShadowReceiver = false

            environment = environmentLoader.createHDREnvironment(
                assetFileLocation = "environments/HDR_040_Field_Env.hdr"
            )!!

            configureSession { _, config ->
                config.depthMode = Config.DepthMode.AUTOMATIC
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            // Session update logic with instruction handling AND plane renderer timer
            onSessionUpdated = { session, frame ->
                if (frame.camera.trackingState == TrackingState.TRACKING) {

                    // --- START NEW CODE for Plane Renderer Timer ---
                    if (!planeRendererTimerStarted) {
                        planeRendererTimerStarted = true // Mark as started
                        planeRendererTimerJob = viewLifecycleOwner.lifecycleScope.launch {
                            delay(10000L) // Wait for 10 seconds (10000 milliseconds)
                            // Check if the view is still valid before accessing planeRenderer
                            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                planeRenderer.isEnabled = false // Disable the dots
                            }
                        }
                    }
                    // --- END NEW CODE ---

                    if (!modelHasBeenPlaced) {
                        showScanningInstructions() // Keep showing if no model placed yet
                        frame.getUpdatedPlanes().firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { plane ->
                                if (anchorNode == null) { // Place only once initially
                                    placeAnchor(plane.createAnchor(plane.centerPose))
                                }
                            }
                    }
                } else {
                    // Optional: Handle tracking loss
                }
            }
        }

        placeModelButton.setOnClickListener {
            toggleModelLockState()
        }
    }

    private fun showScanningInstructions() {
        interactionInstructionJob?.cancel()
        tvInstructions.text = "Find a well-lit surface.\nSlowly move device to scan."
        tvInstructions.visibility = View.VISIBLE
    }

    private fun showInteractionInstructions() {
        interactionInstructionJob?.cancel()
        tvInstructions.text = "Pinch to resize\nTwist to rotate"
        tvInstructions.visibility = View.VISIBLE

        // Note: Your original TexArFragment had a 10-second delay here. Keeping it, adjust if needed.
        interactionInstructionJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(10000) // 10 seconds delay for interaction instructions
            if (tvInstructions.visibility == View.VISIBLE) {
                tvInstructions.visibility = View.GONE
            }
        }
    }

    private fun placeAnchor(anchor: Anchor) {
        if (anchorNode == null) {
            anchorNode = AnchorNode(arSceneView.engine, anchor).apply {
                isEditable = !isModelLocked

                lifecycleScope.launch {
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter/tex.glb" // Verify this path
                    )
                    if (modelInstance != null) {
                        modelHasBeenPlaced = true
                        // Toast.makeText(requireContext(), "Model loaded successfully", Toast.LENGTH_SHORT).show() // Optional Toast

                        modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            centerOrigin = Position(y = -0.5f)
                        ).apply {
                            isEditable = !isModelLocked
                        }
                        addChildNode(modelNode!!)
                        showInteractionInstructions()

                    } else {
                        Toast.makeText(requireContext(), "Failed to load model", Toast.LENGTH_SHORT).show()
                        anchorNode?.let { try { arSceneView.removeChildNode(it) } catch (e: Exception) {} }
                        anchorNode = null
                        modelHasBeenPlaced = false
                        showScanningInstructions()
                    }
                }
                try { arSceneView.addChildNode(this) } catch (e: Exception) {}
            }
        }
    }


    private fun toggleModelLockState() {
        if (anchorNode != null && modelNode != null) {
            isModelLocked = !isModelLocked
            anchorNode?.isEditable = !isModelLocked
            modelNode?.isEditable = !isModelLocked
            val message = if (isModelLocked) "Model interaction locked!" else "Model interaction unlocked!"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            placeModelButton.text = if (isModelLocked) "Unlock Interaction" else "Lock Interaction"
        } else {
            Toast.makeText(requireContext(), "Place the model first!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interactionInstructionJob?.cancel()
        // --- NEW: Cancel the plane renderer timer job ---
        planeRendererTimerJob?.cancel()
        // --- END NEW ---
        arSceneView.destroy()
    }
}