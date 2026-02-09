import cv2 as cv
import cv2.aruco as aruco

import numpy as np

def main():
    #Create our camera object, we pass in either 0 (the default camera) or a path to a specific camera
    camera = cv.VideoCapture(0)


    #Grab the specific dictionary we are working with
    aruco_dictionary = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)
    #define parameters for our detector, we just use default here
    parameters = aruco.DetectorParameters()
    #define our detector, using our dictionary and parameters
    detector = aruco.ArucoDetector(aruco_dictionary, parameters)
    
    #READ VIDEO FROM CAMERA
    #Create our camera object, we pass in either 0 (the default camera) or a path to a specific camera
    camera = cv.VideoCapture(0)

    #Begin showcasing the video
    while True:
        #here frame will be what we scan for our detection
        ret, frame = camera.read()
        if not ret:
            #if we do not return a frame, break out of the loop
            break

        #generate the corners of each marker, the ids of each marker, and any potential rejects to note in each frame
        corners, ids, rejects = detector.detectMarkers(frame)

        #output our detected markers for debug
        print("Detected markers:\n",ids)

        #if there are any marker ids, note and draw them onto the frame
        if ids is not None:
            aruco.drawDetectedMarkers(frame, corners, ids)

        #show the frame
        cv.imshow("Markers", frame)

    #Wait for the user to press the esc key to close the program
        if cv.waitKey(1) == 27:
            break

    #release our camera, and destroy any windows to close cleanly
    camera.release()
    cv.destroyAllWindows()
    
    return


if __name__ == "__main__":
    main()