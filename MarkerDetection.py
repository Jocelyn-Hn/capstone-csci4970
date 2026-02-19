import cv2 as cv
import cv2.aruco as aruco
import numpy as np

MARKER_SIZE = 0.0349       # meters (marker edge length)
M2_TO_IN2 = 1550.003       # m^2 → in^2 conversion
SMOOTHING_ALPHA = 0.2      # EMA smoothing (0.1–0.3 recommended)

camera_matrix = np.load("camera_matrix.npy")
dist_coeffs = np.load("dist_coeffs.npy")

# Stores smoothed 3D positions per marker ID
smoothed_tvecs = {}

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

            rvecs, tvecs, _ = aruco.estimatePoseSingleMarkers(
                corners, MARKER_SIZE, camera_matrix, dist_coeffs
            )

            current_points = {}

            for i, marker_id in enumerate(ids.flatten()):
                tvec = tvecs[i].reshape(3)

                if marker_id not in smoothed_tvecs:
                    smoothed_tvecs[marker_id] = tvec
                else:
                    smoothed_tvecs[marker_id] = (
                        SMOOTHING_ALPHA * tvec +
                        (1 - SMOOTHING_ALPHA) * smoothed_tvecs[marker_id]
                    )

                current_points[marker_id] = smoothed_tvecs[marker_id]

            if len(current_points) >= 3:
                selected_ids = sorted(current_points.keys())[:3]
                points_3d = np.array(
                    [current_points[mid] for mid in selected_ids]
                )

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
