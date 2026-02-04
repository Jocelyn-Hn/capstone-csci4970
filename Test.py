import cv2 as cv
import numpy as np  


MAXHEIGHT = 1200
MAXWIDTH = 800
#Prompt the user and to choose a detection method
print("Please select a detection method: ")
print("1. Sobel\n2. Laplacian\n3. Canny")

methodChoice = int(input("Enter choice [1,2,3]: "))


if methodChoice == 1:
    #load our given image, this one is just loaded via the folder itself
    #image = cv.imread("CVEDImage1.png", cv.IMREAD_GRAYSCALE)
    image = cv.imread("docImg1.jpg", cv.IMREAD_GRAYSCALE)


    #Apply our edge detection operations

    #SOBEL EDGE DETECTION
    #   passes our image, the depth (gradient stuff), 
    #   if we are using a horizontal check, if we are using a vertical check, and the kernel size for calculation
    sobelx = cv.Sobel(image, cv.CV_32F, 1, 0, ksize=3) 
    sobely = cv.Sobel(image, cv.CV_32F, 0, 1, ksize=3)

    #create the magnitude image by overlaying both sobel operations essentially
    mag = cv.magnitude(sobelx,sobely)

    #make the image viewable by converting the scale to unit8 (unsigned 8 bit integer values)
    mag = cv.convertScaleAbs(mag)

    #display the result
    cv.namedWindow("Sobel Edge Detection", cv.WINDOW_NORMAL)
    cv.imshow("Sobel Edge Detection", mag)

elif methodChoice == 2:
    #load our given image, this one is just loaded via the folder itself
    image = cv.imread("CVEDImage1.png", cv.IMREAD_GRAYSCALE)

    #LAPLACIAN EDGE DETECTION
    #   Passes our image, the depth (gradient stuff), and kernel size
    laplacian = cv.Laplacian(image, cv.CV_32F, ksize=3)

    #convert it to unit8 so it is visible
    laplacian_abs = cv.convertScaleAbs(laplacian)

    #display the result
    cv.namedWindow("Laplacian Edge Detection", cv.WINDOW_NORMAL)
    cv.imshow("Laplacian Edge Detection", laplacian_abs)

elif methodChoice == 3:
    #load our given image, this one is just loaded via the folder itself
    image = cv.imread("CVEDImage1.png", cv.IMREAD_GRAYSCALE)

    #CANNY EDGE DETECTION
    #   start by blurring the image using a gaussian blur
    #   passes, the image, the kernel size as a tuple, and a number to control the spread of the blur
    #   this number represents 3 parameters, sigmax, sigmay, and the border, it is highly modifiable and able to be worked with
    blur = cv.GaussianBlur(image, (5,5), 1.4)

    #Apply the canny detection
    #   Passes, the blurred image, and two thresholds that are used for math (look into this on the official opencv page)
    canny = cv.Canny(blur,threshold1=50, threshold2=200)


    #display the result
    cv.namedWindow("Canny Edge Detection", cv.WINDOW_NORMAL)
    cv.imshow("Canny Edge Detection", canny)


#wait for an input then close the window
cv.waitKey(0)
cv.destroyAllWindows()




