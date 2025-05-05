package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle

class woodcutter : Fragment() {

    private lateinit var arSceneView: ARSceneView
    private lateinit var placeModelButton: Button
    private lateinit var btnInfo: ImageButton
    private lateinit var btnHelp: ImageButton
    private lateinit var tvInstructions: TextView
    private lateinit var materialLoader: MaterialLoader
    private var anchorNode: AnchorNode? = null
    private var modelNode: ModelNode? = null
    private var isModelLocked = false


    private var interactionInstructionJob: Job? = null
    private var modelHasBeenPlaced = false

    // variables para sa timer ng white dots
    private var planeRendererTimerJob: Job? = null
    private var planeRendererTimerStarted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_woodcutterar, container, false)
        arSceneView = view.findViewById(R.id.arSceneViewWoodCutter)
        placeModelButton = view.findViewById(R.id.btnPlaceModelWoodCutter)
        btnInfo = view.findViewById(R.id.btnInfoWoodcutter)
        btnHelp = view.findViewById(R.id.btnHelpWoodcutter)
        tvInstructions = view.findViewById(R.id.tvInstructionsWoodcutter)

        materialLoader = MaterialLoader(arSceneView.engine, requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // white dot timer reset to 0
        planeRendererTimerStarted = false


        // function para makita yung instructions initially
        showScanningInstructions()

        setupButtonClickListeners() // functions for all listeners

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
                if (frame.camera.trackingState == TrackingState.TRACKING) {

                    // timer starts here
                    if (!planeRendererTimerStarted) {
                        planeRendererTimerStarted = true
                        planeRendererTimerJob = viewLifecycleOwner.lifecycleScope.launch {
                            delay(5000L) // 10 seconds

                            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                planeRenderer.isEnabled = false // Disable white dots
                            }
                        }
                    }



                    if (!modelHasBeenPlaced) {
                        showScanningInstructions()
                        frame.getUpdatedPlanes().firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { plane ->
                                if (anchorNode == null) {
                                    placeAnchor(plane.createAnchor(plane.centerPose))
                                }
                            }
                    }

                } else {
                    // Could be "tracking lost"
                }
            }
        }
    }

    // show scanning instructions AGAIN
    private fun showScanningInstructions() {
        interactionInstructionJob?.cancel() //
        tvInstructions.text = "Find a well-lit surface.\nSlowly move device to scan."
        tvInstructions.visibility = View.VISIBLE
    }

    private fun showInteractionInstructions() {
        interactionInstructionJob?.cancel()
        tvInstructions.text = "Pinch to resize\nTwist to rotate"
        tvInstructions.visibility = View.VISIBLE

        interactionInstructionJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)
            if (tvInstructions.visibility == View.VISIBLE) {
                tvInstructions.visibility = View.GONE
            }
        }
    }


    private fun setupButtonClickListeners() {
        placeModelButton.setOnClickListener {
            toggleModelLockState()
        }

        btnHelp.setOnClickListener {
            // Show the relevant instruction again, based on current state
            if (modelHasBeenPlaced) {
                showInteractionInstructions()
            } else {
                showScanningInstructions()
            }
        }

        btnInfo.setOnClickListener {
            if (modelNode != null) {
                showInfoDialog()
            } else {
                Toast.makeText(requireContext(), "Place the artifact first to see info", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showInfoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.woodcutter_info_title)
            .setMessage(R.string.woodcutter_info_message)
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }

    // LIFECYCLE STARTS HERE IG FOR AR
    private fun placeAnchor(anchor: Anchor) {
        if (anchorNode == null) {
            anchorNode = AnchorNode(arSceneView.engine, anchor).apply {
                isEditable = !isModelLocked
                lifecycleScope.launch {
                    val modelInstance = arSceneView.modelLoader.loadModelInstance(
                        "file:///android_asset/models/woodcutter/frame.gltf"
                    )
                    if (modelInstance != null) {
                        modelHasBeenPlaced = true

                        modelNode = ModelNode(
                            modelInstance = modelInstance,
                            scaleToUnits = 0.5f,
                            centerOrigin = Position(y = -0.5f)
                        ).apply { isEditable = !isModelLocked }

                        addChildNode(modelNode!!)

                        showInteractionInstructions()

                    } else {
                        Toast.makeText(requireContext(), "Failed to load model", Toast.LENGTH_SHORT).show()
                        anchorNode?.let {
                            try { arSceneView.removeChildNode(it) } catch (e: Exception) { /* Ignore */ }
                        }
                        anchorNode = null
                        modelHasBeenPlaced = false // Reset flag
                        showScanningInstructions() // Show scanning instructions again if load fails
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
        planeRendererTimerJob?.cancel()
        arSceneView.destroy()
    }
}