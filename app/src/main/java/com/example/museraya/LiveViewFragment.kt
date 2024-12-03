package com.example.museraya

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment

class LiveViewFragment : Fragment() {

    private val TAG = "LiveViewFragment"
    private lateinit var arFragment: ArFragment
    private lateinit var arButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liveview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize AR fragment and button after the view is created
        arFragment = childFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        arButton = view.findViewById(R.id.ar_button)

        arButton.setOnClickListener {
            startARSession()
        }

        // Set up the listener for tap gestures on the AR surface
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            Log.d(TAG, "Tap detected on the AR plane.")
            // Create an anchor at the tapped location
            val anchor = hitResult.createAnchor()
            place3DModelAtAnchor(anchor)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            CameraPermissionHelper.requestCameraPermission(requireActivity())
        }
    }

    // Function to start the AR session
    private fun startARSession() {
        Log.d(TAG, "AR session started.")
        // You can add more session setup here if needed
    }

    // Function to place the 3D model at the given anchor
    private fun place3DModelAtAnchor(anchor: Anchor) {
        Log.d(TAG, "Attempting to load the model...")

        // Use the correct path to the .gltf model in the assets folder
        ModelRenderable.builder()
            .setSource(context, Uri.parse("models/rubber_duck_toy_4k.gltf")) // Path to the .gltf file in assets folder
            .build()
            .thenAccept { modelRenderable ->
                Log.d(TAG, "Model loaded successfully.")

                // Create AnchorNode to attach the model to the anchor
                val anchorNode = AnchorNode(anchor)
                anchorNode.renderable = modelRenderable
                anchorNode.setParent(arFragment.arSceneView.scene)

                // Optional: Scale the model to a suitable size
                anchorNode.localScale = Vector3(0.2f, 0.2f, 0.2f)

                Log.d(TAG, "Model placed at anchor.")
            }
            .exceptionally { throwable ->
                Log.e(TAG, "Error loading model: ", throwable)

                // Display user feedback in case of failure
                activity?.runOnUiThread {
                    Toast.makeText(context, "Failed to load 3D model: ${throwable.localizedMessage}", Toast.LENGTH_SHORT).show()
                }

                null
            }
    }
}

object CameraPermissionHelper {
    fun hasCameraPermission(activity: androidx.fragment.app.FragmentActivity): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(activity: androidx.fragment.app.FragmentActivity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private const val CAMERA_PERMISSION_REQUEST_CODE = 0
}
