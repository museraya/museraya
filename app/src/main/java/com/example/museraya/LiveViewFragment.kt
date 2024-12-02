package com.example.museraya

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
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
    }

    override fun onResume() {
        super.onResume()
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            CameraPermissionHelper.requestCameraPermission(requireActivity())
        }
    }

    private fun startARSession() {
        val frame: Frame? = arFragment.arSceneView?.arFrame

        if (frame == null) {
            Log.e(TAG, "AR frame is null")
            return
        }

        val hitResult = frame.hitTest(
            arFragment.arSceneView.width / 2f,
            arFragment.arSceneView.height / 2f
        ).firstOrNull()

        if (hitResult != null) {
            val anchor = hitResult.createAnchor()
            place3DModelAtAnchor(anchor)
        } else {
            Log.d(TAG, "No hit detected")
        }
    }

    private fun place3DModelAtAnchor(anchor: Anchor) {
        ModelRenderable.builder()
            .setSource(context, Uri.parse("donut.obj"))
            .build()
            .thenAccept { modelRenderable ->
                val anchorNode = AnchorNode(anchor)
                anchorNode.renderable = modelRenderable
                anchorNode.setParent(arFragment.arSceneView.scene)
                anchorNode.localScale = Vector3(0.2f, 0.2f, 0.2f)
            }
            .exceptionally { throwable ->
                Log.e(TAG, "Error loading model", throwable)
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
