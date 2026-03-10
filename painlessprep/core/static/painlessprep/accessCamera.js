const video = document.getElementById("cameraStream");      //Grab the <video> block with id cameraStream 
const canvas = document.getElementById("canvas");           //grab the <canvas> block where the video will be output


//Prompt the user to give access to the camera, then, stream that camera to the given <video> HTML block
navigator.mediaDevices.getUserMedia({video : true})
        .then(stream => {video.srcObject = stream;}) //Pass stream in as the video source object
        .catch(err => {console.error("Camera Error: ", err);} //if an error occurs, pass that error to the console error
        );







        
