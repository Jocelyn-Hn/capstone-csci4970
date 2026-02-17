import cv2 as cv
import cv2.aruco as aruco
import numpy as np

MARKER_SIZE = 0.0349  # meters (example: 5 cm)
M2_TO_IN2 = 1550.003 #conversion

# ---- LOAD CAMERA CALIBRATION ----
camera_matrix = np.load("camera_matrix.npy")
dist_coeffs = np.load("dist_coeffs.npy")

def triangle_area_3d(p1, p2, p3):
    return 0.5 * np.linalg.norm(np.cross(p2 - p1, p3 - p1))

def main():
    camera = cv.VideoCapture(0)

    aruco_dict = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)
    parameters = aruco.DetectorParameters()
    detector = aruco.ArucoDetector(aruco_dict, parameters)

    while True:
        ret, frame = camera.read()
        if not ret:
            break

        corners, ids, _ = detector.detectMarkers(frame)

        if ids is not None and len(ids) >= 3:
            aruco.drawDetectedMarkers(frame, corners, ids)

            # Pose estimation
            rvecs, tvecs, _ = aruco.estimatePoseSingleMarkers(
                corners, MARKER_SIZE, camera_matrix, dist_coeffs
            )

            # Use first 3 detected markers
            points_3d = tvecs[:3].reshape(3, 3)

            # Compute pairwise distances
            d01 = np.linalg.norm(points_3d[0] - points_3d[1])
            d12 = np.linalg.norm(points_3d[1] - points_3d[2])
            d02 = np.linalg.norm(points_3d[0] - points_3d[2])

            # Identify diagonal
            if d01 >= d12 and d01 >= d02:
                pA, pB, pC = points_3d[0], points_3d[1], points_3d[2]
            elif d12 >= d01 and d12 >= d02:
                pA, pB, pC = points_3d[1], points_3d[2], points_3d[0]
            else:
                pA, pB, pC = points_3d[0], points_3d[2], points_3d[1]

            # Triangle + window area
            triangle_area = triangle_area_3d(pA, pB, pC)
            window_area_m2 = 2 * triangle_area
            window_area_in2 = window_area_m2 * M2_TO_IN2

            cv.putText(
                frame,
                f"Window Area: {window_area_in2:.1f} in^2",
                (20, 40),
                cv.FONT_HERSHEY_SIMPLEX,
                1,
                (0, 255, 0),
                2
            )

        cv.imshow("Markers", frame)

        if cv.waitKey(1) == 27:
            break

    camera.release()
    cv.destroyAllWindows()

if __name__ == "__main__":
    main()
