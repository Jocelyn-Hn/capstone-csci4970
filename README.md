# Painless Prep Application

## About
The Painless Prep Application is a developing application intended to reduce the time and energy investment needed during the window-measuring process for painters so that they can be properly be covered during the painting process. 

Planned features: 
    - Ability to quickly scan windows via AruCo markers using OpenCV to estimate proper window dimensions
    - Ability to group windows together as "rooms" that allow for neat and orderly processing from a third party
    - Ability to measure windows with 2 or fewer reference markers

# Group Members
Jocelyn Horn<br/>
Josef Stanek<br/>
Kurtis Kathol<br/>

# Release notes
    - V 1.0.0-Alpha
        - Added "Generate_Aruco.py", an AruCo generation script that allows for easy creation of AruCo markers 
        - Added "MarkerDetection.py", the main script that detects, and roughly estimates the distance between AruCo markers
        - Added "calibrate_camera.py", a camera calibration script that allows for more accurate distance estimation.
        - Removed Test.py, TestVideo.py, VideoCapture.py, as those files were generic tests that provided no use to the release
        
