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
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.ArucoDetector
import org.opencv.objdetect.Dictionary
import org.opencv.objdetect.Objdetect
import org.opencv.objdetect.DetectorParameters

// Main activity implements OpenCV camera
class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {


    private lateinit var cameraView: JavaCameraView
    private lateinit var arucoDetector: ArucoDetector
    private lateinit var dictionary: Dictionary
    private lateinit var parameters: DetectorParameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            println("OpenCV failed to load")
        } else {
            println("OpenCV loaded successfully")
        }
        dictionary = Objdetect.getPredefinedDictionary(Objdetect.DICT_4X4_50)
        parameters = DetectorParameters()
        arucoDetector = ArucoDetector(dictionary, parameters)

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

        // Disable camera when app is paused
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
    }

    // Called when camera stops
    override fun onCameraViewStopped() {
        // Cleanup resources here
    }

    // Called for every camera frame
    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {

        val rgba = inputFrame.rgba()
        val gray = inputFrame.gray()

        val corners = ArrayList<Mat>()
        val ids = Mat()

        // Detect markers
        arucoDetector.detectMarkers(gray, corners, ids)

        // display markers
        if (!ids.empty()) {
            for (i in corners.indices) {
                val corner = corners[i]

                // Convert corners to points
                val points = MatOfPoint(
                    Point(corner.get(0, 0)[0], corner.get(0, 0)[1]),
                    Point(corner.get(0, 1)[0], corner.get(0, 1)[1]),
                    Point(corner.get(0, 2)[0], corner.get(0, 2)[1]),
                    Point(corner.get(0, 3)[0], corner.get(0, 3)[1])
                )

                // Draw marker outline
                Imgproc.polylines(
                    rgba,
                    listOf(points),
                    true,
                    Scalar(0.0, 255.0, 0.0),
                    3
                )

                // Draw marker ID
                val centerX = (corner.get(0,0)[0] + corner.get(0,2)[0]) / 2
                val centerY = (corner.get(0,0)[1] + corner.get(0,2)[1]) / 2

                Imgproc.putText(
                    rgba,
                    "ID: ${ids[i, 0][0].toInt()}",
                    Point(centerX, centerY),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    1.0,
                    Scalar(255.0, 0.0, 0.0),
                    2
                )
            }
        }

        return rgba
    }
}