package com.example.painlessprep

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.calib3d.Calib3d
import org.opencv.core.Mat
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.ArucoDetector
import org.opencv.objdetect.Dictionary
import org.opencv.objdetect.Objdetect
import org.opencv.objdetect.DetectorParameters
import android.widget.Button


// Main activity implements OpenCV camera
class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {


    private lateinit var cameraView: JavaCameraView
    private lateinit var arucoDetector: ArucoDetector
    private lateinit var dictionary: Dictionary
    private lateinit var parameters: DetectorParameters
    private var savedCalibration: Pair<Mat,Mat>? = null

    //Boolean to check if we want to calibrate
    var isCalibrating = false
    //Boolean check to see if we are currently calibrating
    var isProcessingCalibration = false
    //Chessboard Square size (mine printed out to ~22mm per square
    val calibSquareSize = .022 //22MM
    //Amount of frames to take when we calibrate, 20-30 if good practice for calibration
    val requiredFrames = 25
    //The size of the chessboard, mine is 10x7 squares, which means its a 9x6 chessboard
    val boardSize = Size(9.0,6.0)

    //Our lists that store our calibration data.
    val collectedImagePoints = mutableListOf<MatOfPoint2f>()
    val collectedObjectPoints = mutableListOf<MatOfPoint3f>()

    //used for calibration delay
    var lastCaptureTime = 0L







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
        savedCalibration = loadCalibration()
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

        val btnCalibrate = findViewById<Button>(R.id.btn_calibrate)
        btnCalibrate.setOnClickListener {
            isCalibrating = true
            collectedObjectPoints.clear()
            collectedImagePoints.clear()
            runOnUiThread {
                Toast.makeText(this,"Beginning calibration, please keep a chessboard in camera view and move around!", Toast.LENGTH_LONG).show()
            }

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

        if(savedCalibration != null) {
            val (cameraMatrix, distanceCoeffs) = savedCalibration!!

        }

        //run calibration code, make sure we arent already in the process of calibrating
        if (isCalibrating && !isProcessingCalibration) {
            //grab out current time to know if we should capture a frame
            val currentTime = System.currentTimeMillis()
            //start by creating an array for the corners, and search for a chessboard
            val corners = MatOfPoint2f()
            val foundBoard = Calib3d.findChessboardCorners(
                gray, boardSize, corners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH or
                Calib3d.CALIB_CB_NORMALIZE_IMAGE or
                Calib3d.CALIB_CB_FAST_CHECK)

            if (foundBoard && currentTime - lastCaptureTime > 3000) {
                //if we have found a board, and we are past the time threshold (0.33 fps)
                lastCaptureTime = currentTime

                //Let the user know we've found a chessboard, and how many frames are left to capture
                runOnUiThread {
                    Toast.makeText(this, "Chessboard Found, (${collectedObjectPoints.size + 1}/$requiredFrames)", Toast.LENGTH_SHORT).show()
                }
                //if we find a chessboard, refine the corners for more accuracy
                Imgproc.cornerSubPix(
                    gray, corners, Size(11.0, 11.0), Size(-1.0, -1.0),
                    TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 30, 0.1)
                )
                //Based off that refinement, add those  points to collected image points
                collectedImagePoints.add(corners)

                //Now, we generate 3D object points
                val objPoints = MatOfPoint3f()
                val points = mutableListOf<Point3>()
                //loop through the board size to fill points
                for (i in 0 until boardSize.height.toInt()) {
                    for (j in 0 until boardSize.width.toInt()) {
                        points.add(Point3(j * calibSquareSize, i * calibSquareSize, 0.0))
                    }
                }
                //Add the found points to our object points list
                objPoints.fromList(points)
                collectedObjectPoints.add(objPoints)

                //draw the board on our frame
                Calib3d.drawChessboardCorners(rgba, boardSize, corners, foundBoard)

                //Start a secondary thread to run calibration (supposedly should lighten the load)
                if (collectedObjectPoints.size >= requiredFrames && !isProcessingCalibration) {
                    isCalibrating = false
                    isProcessingCalibration = true
                    Thread {
                        runCalibration(rgba.size())
                    }.start()


                }

            }



        } else { //If we aren't calibrating, run the aruco detection code!
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
        }
        return rgba
    }

    //Function that actually runs the calibration
    fun runCalibration(imageSize : Size) {

        //Cast the image and object points as standard opencv mats
        val objectPointCast = collectedObjectPoints.map { it as Mat }
        val imagePointCast = collectedImagePoints.map { it as Mat }


        val cameraMatrix = Mat.zeros(3, 3, CvType.CV_64F)
        val distanceCoeffs = Mat.zeros(5, 1, CvType.CV_64F)
        val rvecs = mutableListOf<Mat>()
        val tvecs = mutableListOf<Mat>()

        //Run the calibration using
        val rms = Calib3d.calibrateCamera(
            objectPointCast,
            imagePointCast,
            imageSize,
            cameraMatrix,
            distanceCoeffs,
            rvecs,
            tvecs
        )
        runOnUiThread {
            Toast.makeText(this, "Calibration Finished! RMS ERROR: $rms", Toast.LENGTH_LONG).show()
        }

        //save our calibration data for future use
        saveCalibration(cameraMatrix, distanceCoeffs)
    }

    //Function to save calibration data
    fun saveCalibration(cameraMatrix: Mat, distanceCoeffs: Mat) {
        //grab our current preference data
        val prefs = getSharedPreferences("CameraPrefs", MODE_PRIVATE)

        //fill cameraArray with our camera matrix
        val cameraArray = DoubleArray(9)
        cameraMatrix.get(0,0, cameraArray)

        //fill distArray with our distance coeffecients
        val distArray = DoubleArray(distanceCoeffs.total().toInt())
        distanceCoeffs.get(0,0,distArray)

        //Crete our JSON object that will act as our storage
        val json = JSONObject()
        json.put("cameraMatrix", JSONArray(cameraArray.toList()))
        json.put("distanceCoeffs", JSONArray(distArray.toList()))

        prefs.edit().putString("calibration", json.toString()).apply()
    }

    fun loadCalibration(): Pair<Mat, Mat>? {
        //dig into the preferences and grab our calibration preferences
        val prefs = getSharedPreferences("CameraPrefs", MODE_PRIVATE)
        val jsonString = prefs.getString("calibration", null) ?: return null
        val json = JSONObject(jsonString)

        //Load the camera matrix data
        val cameraArray = json.getJSONArray("cameraMatrix")
        val cameraMatrix = Mat(3,3, CvType.CV_64F)
        for( i in 0 until 3) {
            for (j in 0 until 3) {
                cameraMatrix.put(i , j , cameraArray.getDouble(i * 3 + j))
            }
        }

        //Load the distance coefficients data
        val distArray = json.getJSONArray("distanceCoeffs")
        val distanceCoeffs = Mat(distArray.length(), 1, CvType.CV_64F)
        for(i in 0 until distArray.length()) {
            distanceCoeffs.put(i, 0, distArray.getDouble(i))
        }

        //return the data for usage
        return Pair(cameraMatrix, distanceCoeffs)

    }
}