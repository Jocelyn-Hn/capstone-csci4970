import cv2 as cv
import cv2.aruco as aruco
import numpy as np

def generate_markers():
    #Define the dictionary we want to use for marker generation
    aruco_dictionary = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)

    #Create only 2 markers (should only need the two for the windows max)
    for marker_id in range(2):
        
        #Define marker size, 200 pixels should be enough
        marker_size = 200

        #Create the actual marker image
        marker_image = aruco.generateImageMarker(aruco_dictionary, marker_id, marker_size)
        
        #Store the marker in the dedicated AruCo_markers folder, name it based off its ID
        cv.imwrite(f"AruCo_Markers/aruco_{marker_id}.png", marker_image)
        

if __name__ == "__main__":
    generate_markers()