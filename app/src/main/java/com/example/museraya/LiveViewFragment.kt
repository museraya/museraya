package com.example.museraya

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class LiveViewFragment : Fragment() {

    private val TAG = "LiveViewFragment"
    private lateinit var surfaceView: SurfaceView
    private var cameraDevice: CameraDevice? = null
    private lateinit var cameraCaptureSession: CameraCaptureSession

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liveview, container, false)

        // Initialize the SurfaceView to display the camera feed
        surfaceView = view.findViewById(R.id.surface_view)

        return view
    }

    override fun onResume() {
        super.onResume()

        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            CameraPermissionHelper.requestCameraPermission(requireActivity())
            return
        }

        // Start the camera after permission is granted
        startCamera()
    }

    private fun startCamera() {
        val cameraManager = requireActivity().getSystemService(CameraManager::class.java)
        try {
            val cameraId = cameraManager.cameraIdList[0] // Assuming the first camera
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        startPreview()
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        cameraDevice?.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        Log.e(TAG, "Camera error: $error")
                    }
                }, null)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Camera access error", e)
        }
    }

    private fun startPreview() {
        try {
            // Get the Surface from the SurfaceView's holder
            val surface = surfaceView.holder.surface

            // Create a capture session to display the preview
            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session

                        // Start the camera preview
                        val previewRequest = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                        previewRequest.addTarget(surface)
                        cameraCaptureSession.setRepeatingRequest(previewRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Camera configuration failed")
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error starting camera preview", e)
        }
    }


    override fun onPause() {
        super.onPause()

        // Release the camera when the fragment is paused
        cameraDevice?.close()
        cameraDevice = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Camera permission is needed to run this application", Toast.LENGTH_LONG).show()
        } else {
            // Permission granted, start the camera
            startCamera()
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