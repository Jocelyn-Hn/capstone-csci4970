import cv2 as cv
import cv2.aruco as aruco

import numpy as np

def main():
    #Create our camera object, we pass in either 0 (the default camera) or a path to a specific camera
    camera = cv.VideoCapture(0)


    #Grab the specific dictionary we are working with
    aruco_dictionary = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)
    parameters = aruco.DetectorParameters()
    detector = aruco.ArucoDetector(aruco_dictionary, parameters)
    
    #READ VIDEO FROM CAMERA
    #Create our camera object, we pass in either 0 (the default camera) or a path to a specific camera
    camera = cv.VideoCapture(0)

    #Begin showcasing the video
    while True:
        #here frame will be what we scan for our detection
        ret, frame = camera.read()
        if not ret:
            break

        
        corners, ids, rejects = detector.detectMarkers(frame)

        print("Detected markers:\n",ids)
        if ids is not None:
            aruco.drawDetectedMarkers(frame, corners, ids)
        cv.imshow("Markers", frame)

    #Wait for the user to press the esc key to close the program
        if cv.waitKey(1) == 27:
            break

    camera.release()
    cv.destroyAllWindows()
    
    return


if __name__ == "__main__":
    main()