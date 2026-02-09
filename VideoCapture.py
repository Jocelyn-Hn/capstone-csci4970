import cv2 as cv
import numpy as np


def RunCamera():
    #Create our camera object, we pass in either 0 (the default camera) or a path to a specific camera
    camera = cv.VideoCapture(0)

    # if not camera.isOpened():
    #     exit()

    #Obtain the cameras width and height


    # #UNCOMMENT TO RECORD VIDEO
    # #Encode video and define a videowriter
    # fourcc = cv.VideoWriter.fourcc(*"XVID")
    # #create our output video
    # out = cv.VideoWriter("output.avi", fourcc, 20.0, (int(frame_width), int(frame_height)))

    #Begin showcasing the video
    while True:
        ret, frame = camera.read()

        ##UNCOMMENT TO RECORD VIDEO
        # #Write the read frame to our output
        # out.write(frame)

        #Display the frame
        cv.imshow("Camera", frame)

        #PROCESSING GOES HERE 
        #We should be able to do any processing here, aruco markers, measuring, ect ect..

        #Wait for the user to press the esc key to close the program
        if cv.waitKey(1) == 27:
            break


    #Release the camera and output video, close the window
    camera.release()
    #UNCOMMENT TO RECORD VIDEO
    # out.release()
    cv.destroyAllWindows()
    return 0


if __name__ == "__main__":
    RunCamera()