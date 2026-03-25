package com.example.painlessprep

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

// Main activity implements OpenCV camera
class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {


    private lateinit var cameraView: JavaCameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            println("OpenCV failed to load")
        } else {
            println("OpenCV loaded successfully")
        }

        // Set the UI layout
        setContentView(R.layout.activity_main)

        // Link the camera view from XML layout
        cameraView = findViewById(R.id.camera_view)

        // Make sure the camera view is visible
        cameraView.visibility = SurfaceView.VISIBLE


        cameraView.setCvCameraViewListener(this)

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            // If permission already granted, enable camera
            cameraView.setCameraPermissionGranted()
            cameraView.enableView()
        }
    }

    override fun onResume() {
        super.onResume()

        // Re-initialize OpenCV when returning to app
        if (OpenCVLoader.initDebug()) {
            cameraView.setCameraPermissionGranted()
            cameraView.enableView()
        }
    }

    override fun onPause() {
        super.onPause()

        // Disable camera when app is paused (saves resources)
        cameraView.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Release camera when activity is destroyed
        cameraView.disableView()
    }

    // Handle result of permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // If permission granted, enable camera
        if (requestCode == 1 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            cameraView.setCameraPermissionGranted()
            cameraView.enableView()
        }
    }

    // Called when camera starts
    override fun onCameraViewStarted(width: Int, height: Int) {
        // No initialization currently
    }

    // Called when camera stops
    override fun onCameraViewStopped() {
        // Cleanup resources here if needed
    }

    // Called for every camera frame
    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {


        return inputFrame.rgba()
    }
}