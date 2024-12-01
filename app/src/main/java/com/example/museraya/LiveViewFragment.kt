package com.example.museraya

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.core.Config

class LiveViewFragment : Fragment() {

    private val TAG = "LiveViewFragment"
    private var session: Session? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liveview, container, false)
    }

    override fun onResume() {
        super.onResume()

        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            CameraPermissionHelper.requestCameraPermission(requireActivity())
            return
        }

        // Check ARCore installation and version status
        if (isARCoreSupportedAndUpToDate()) {
            Toast.makeText(requireContext(), "ARCore is supported and up to date!", Toast.LENGTH_SHORT).show()

            // Create and initialize the AR session
            createSession()
        } else {
            Toast.makeText(requireContext(), "ARCore is not supported or needs an update.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSession() {
        try {
            // Create a new ARCore session
            session = Session(requireContext())

            // Create a session config
            val config = Config(session)

            // Do feature-specific operations here, such as enabling depth or turning on support for Augmented Faces
            // For example, enabling depth:
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE

            // Configure the session
            session?.configure(config)

            Log.i(TAG, "AR session created and configured.")
        } catch (e: UnavailableException) {
            Log.e(TAG, "Failed to create AR session: ARCore not available", e)
        }
    }

    private fun isARCoreSupportedAndUpToDate(): Boolean {
        return when (ArCoreApk.getInstance().checkAvailability(requireContext())) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    // Request ARCore installation or update if needed
                    when (ArCoreApk.getInstance().requestInstall(requireActivity(), true)) {
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            Log.i(TAG, "ARCore installation requested.")
                            false
                        }
                        ArCoreApk.InstallStatus.INSTALLED -> true
                    }
                } catch (e: UnavailableException) {
                    Log.e(TAG, "ARCore not installed", e)
                    false
                }
            }
            ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                Log.e(TAG, "This device does not support ARCore.")
                false
            }
            ArCoreApk.Availability.UNKNOWN_CHECKING -> {
                Log.i(TAG, "ARCore is checking availability. Please retry.")
                false
            }
            ArCoreApk.Availability.UNKNOWN_ERROR, ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                Log.e(TAG, "Error checking ARCore availability.")
                false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            Toast.makeText(requireContext(), "Camera permission is needed to run this application", Toast.LENGTH_LONG).show()

            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(requireActivity())) {
                // Permission denied with "Do not ask again" checked
                CameraPermissionHelper.launchPermissionSettings(requireActivity())
            }
            requireActivity().finish()
        }
    }

    override fun onPause() {
        super.onPause()

        // Release AR session when the fragment is paused or no longer in use.
        session?.close()
        session = null

        Log.i(TAG, "AR session closed and resources released.")
    }
}

object CameraPermissionHelper {
    fun hasCameraPermission(activity: androidx.fragment.app.FragmentActivity): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(activity: androidx.fragment.app.FragmentActivity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    fun shouldShowRequestPermissionRationale(activity: androidx.fragment.app.FragmentActivity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
    }

    fun launchPermissionSettings(activity: androidx.fragment.app.FragmentActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    private const val CAMERA_PERMISSION_REQUEST_CODE = 0
}