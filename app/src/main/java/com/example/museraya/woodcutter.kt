package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton // Import ImageButton
import android.widget.TextView // Import TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Import AlertDialog
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

class woodcutter : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var btnInfo: ImageButton // Info button reference
    private lateinit var btnHelp: ImageButton // Help button reference
    private lateinit var tvInstructions: TextView // Instruction TextView reference
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null
    private var modelNode: ModelNode? = null
    private var isModelLocked = false

    // Instruction-related variables (Restored)
    private var interactionInstructionJob: Job? = null
    private var modelHasBeenPlaced = false

    // --- NEW: Variables for Plane Renderer Timer ---
    private var planeRendererTimerJob: Job? = null
    private var planeRendererTimerStarted = false
    // --- END NEW ---

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_woodcutterar, container, false)
        arSceneView = view.findViewById(R.id.arSceneViewWoodCutter)
        placeModelButton = view.findViewById(R.id.btnPlaceModelWoodCutter)
        btnInfo = view.findViewById(R.id.btnInfoWoodcutter)
        btnHelp = view.findViewById(R.id.btnHelpWoodcutter)
        tvInstructions = view.findViewById(R.id.tvInstructionsWoodcutter) // Use unique ID

        materialLoader = MaterialLoader(arSceneView.engine, requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- NEW: Reset timer flag on view creation ---
        planeRendererTimerStarted = false
        // --- END NEW ---

        // Show initial scanning instruction (Restored)
        showScanningInstructions()

        setupButtonClickListeners() // Setup listeners for ALL buttons

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
                            delay(5000L) // Wait for 10 seconds (10000 milliseconds)
                            // Check if the view is still valid before accessing planeRenderer
                            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                planeRenderer.isEnabled = false // Disable the dots
                            }
                        }
                    }
                    // --- END NEW CODE ---


                    if (!modelHasBeenPlaced) {
                        showScanningInstructions() // Keep showing scanning instructions
                        // Try to find a plane and place the anchor
                        frame.getUpdatedPlanes().firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { plane ->
                                if (anchorNode == null) {
                                    placeAnchor(plane.createAnchor(plane.centerPose))
                                }
                            }
                    }
                    // If model IS placed, interaction instructions are handled by showInteractionInstructions timeout
                } else {
                    // Optional: showScanningInstructions() or "Tracking Lost"
                    // Consider resetting planeRendererTimerStarted here if you want the timer to restart
                    // upon regaining tracking after loss. For now, it only runs once.
                }
            }
        }
    }

    // Restored Instruction Methods
    private fun showScanningInstructions() {
        interactionInstructionJob?.cancel() // Cancel hiding job if active
        tvInstructions.text = "Find a well-lit surface.\nSlowly move device to scan."
        tvInstructions.visibility = View.VISIBLE
    }

    private fun showInteractionInstructions() {
        interactionInstructionJob?.cancel()
        tvInstructions.text = "Pinch to resize\nTwist to rotate"
        tvInstructions.visibility = View.VISIBLE

        interactionInstructionJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)
            // Check if still visible before hiding, in case user manually showed again
            if (tvInstructions.visibility == View.VISIBLE) {
                tvInstructions.visibility = View.GONE
            }
        }
    }
    // --- End Restored Methods ---

    private fun setupButtonClickListeners() {
        placeModelButton.setOnClickListener {
            toggleModelLockState()
        }

        btnHelp.setOnClickListener {
            // Show the relevant instruction again, based on current state
            if (modelHasBeenPlaced) {
                showInteractionInstructions() // Show pinch/rotate and start timer
            } else {
                showScanningInstructions() // Show scanning instructions
            }
        }

        btnInfo.setOnClickListener {
            // Only show info if the model is actually placed
            if (modelNode != null) {
                showInfoDialog()
            } else {
                Toast.makeText(requireContext(), "Place the artifact first to see info", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Added Info Dialog Method
    private fun showInfoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.woodcutter_info_title) // Specific title
            .setMessage(R.string.woodcutter_info_message) // Specific message
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }

    // Updated Place Anchor Method (Restored instruction call)
    private fun placeAnchor(anchor: Anchor) {
        if (anchorNode == null) {
            anchorNode = AnchorNode(arSceneView.engine, anchor).apply {
                isEditable = !isModelLocked
                lifecycleScope.launch {
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter/frame.gltf"
                    )
                    if (modelInstance != null) {
                        modelHasBeenPlaced = true // Mark model as placed (Restored)

                        modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            centerOrigin = Position(y = -0.5f)
                        ).apply { isEditable = !isModelLocked }

                        addChildNode(modelNode!!)

                        // Show interaction instructions when model is placed (Restored)
                        showInteractionInstructions()

                    } else {
                        Toast.makeText(requireContext(), "Failed to load model", Toast.LENGTH_SHORT).show()
                        anchorNode?.let {
                            try { arSceneView.removeChildNode(it) } catch (e: Exception) { /* Ignore */ }
                        }
                        anchorNode = null
                        modelHasBeenPlaced = false // Reset flag (Restored)
                        showScanningInstructions() // Show scanning instructions again if load fails (Restored)
                    }
                }
                try { arSceneView.addChildNode(this) } catch (e: Exception) { /* Ignore */ }
            }
        }
    }

    // Unchanged Toggle Lock State Method
    private fun toggleModelLockState() {
        if (anchorNode != null && modelNode != null) {
            isModelLocked = !isModelLocked
            anchorNode?.isEditable = !isModelLocked
            modelNode?.isEditable = !isModelLocked
            val message = if (isModelLocked) "Model interaction locked!" else "Model interaction unlocked!"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            placeModelButton.text = if (isModelLocked) "Unlock Interaction" else "Lock Interaction"
        } else {
            Toast.makeText(requireContext(), "Place the artifact first!", Toast.LENGTH_SHORT).show()
        }
    }

    // Restored Destroy View Method
    override fun onDestroyView() {
        super.onDestroyView()
        interactionInstructionJob?.cancel()
        // --- NEW: Cancel the plane renderer timer job ---
        planeRendererTimerJob?.cancel()
        // --- END NEW ---
        arSceneView.destroy()
    }
}