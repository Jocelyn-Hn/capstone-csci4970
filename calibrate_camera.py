import cv2 as cv
import numpy as np
import glob

# -------- SETTINGS --------
CHESSBOARD_SIZE = (5, 7)   # inner corners
SQUARE_SIZE = 0.03968        # meters (2.5 cm per square)

# Termination criteria
criteria = (cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER, 30, 0.001)

# Prepare object points (0,0,0), (1,0,0) ... scaled by square size
objp = np.zeros((CHESSBOARD_SIZE[0] * CHESSBOARD_SIZE[1], 3), np.float32)
objp[:, :2] = np.mgrid[0:CHESSBOARD_SIZE[0],
                       0:CHESSBOARD_SIZE[1]].T.reshape(-1, 2)
objp *= SQUARE_SIZE

objpoints = []  # 3D real-world points
imgpoints = []  # 2D image points

camera = cv.VideoCapture(0)

print("Press SPACE to capture calibration frame")
print("Press ESC to finish calibration")

while True:
    ret, frame = camera.read()
    if not ret:
        break

    gray = cv.cvtColor(frame, cv.COLOR_BGR2GRAY)
    found, corners = cv.findChessboardCorners(gray, CHESSBOARD_SIZE, None)

    if found:
        corners2 = cv.cornerSubPix(
            gray, corners, (11, 11), (-1, -1), criteria
        )
        cv.drawChessboardCorners(frame, CHESSBOARD_SIZE, corners2, found)

    cv.imshow("Calibration", frame)
    key = cv.waitKey(1)

    if key == 32 and found:  # SPACE
        objpoints.append(objp)
        imgpoints.append(corners2)
        print(f"Captured frame {len(objpoints)}")

    elif key == 27:  # ESC
        break

camera.release()
cv.destroyAllWindows()

# -------- CALIBRATE --------
ret, camera_matrix, dist_coeffs, rvecs, tvecs = cv.calibrateCamera(
    objpoints, imgpoints, gray.shape[::-1], None, None
)

print("\nCamera Matrix:\n", camera_matrix)
print("\nDistortion Coefficients:\n", dist_coeffs)

# Save results
np.save("camera_matrix.npy", camera_matrix)
np.save("dist_coeffs.npy", dist_coeffs)

print("\nSaved camera_matrix.npy and dist_coeffs.npy")
