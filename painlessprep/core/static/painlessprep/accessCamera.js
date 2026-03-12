const video = document.getElementById("cameraStream");      //Grab the <video> block with id cameraStream 
const canvas = document.getElementById("canvas");           //grab the <canvas> block where the video will be output
const ctx = canvas.getContext("2d");
let intervalActive;
let fps = 15;

// import { blobFrame } from "./blobJpegAndSend";


//Prompt the user to give access to the camera, then, stream that camera to the given <video> HTML block
async function startVideo()
{
        try{
                //Pass stream in as the video source object
                const stream = await navigator.mediaDevices.getUserMedia({video : true});
                video.srcObject = stream;
        } 
        catch (err){
                //if an error occurs, pass that error to the console error
                console.error("Camera Error: ",err);
        }
     
}

function captureFrame()
{
        

        ctx.drawImage(video,0,0);

        // blobFrame(canvas);
        
}

function beginCapture()
{
        if(!intervalActive)
        {
                intervalActive = setInterval(captureFrame, 1000/fps);
        }
        else
        {
                alert("Capturing has already begun.");
        }
        
}  

function stopCapture()
{
    if(!intervalActive)
        {
                alert("Capture has not been started.");
        }
        else
        {
                clearInterval(intervalActive);
                intervalActive = null;
                ctx.clearRect(0,0,canvas.width,canvas.height);
        }    
}

startVideo();








        
