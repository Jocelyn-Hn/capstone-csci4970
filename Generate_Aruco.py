import cv2 as cv
import cv2.aruco as aruco
import numpy as np

def generate_markers():

    aruco_dictionary = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)

    for marker_id in range(2):
        
        marker_size = 200

        marker_image = aruco.generateImageMarker(aruco_dictionary, marker_id, marker_size)
        
        cv.imwrite(f"AruCo_Markers/aruco_{marker_id}.png", marker_image)
        

if __name__ == "__main__":
    generate_markers()