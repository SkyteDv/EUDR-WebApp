// qrScanner.js

const videoElement = document.getElementById('webcamVideo');
const startScanButton = document.getElementById('startScanButton');
const qrOutput = document.getElementById('qrOutput');
let videoStream = null;

function startCamera() {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(function (stream) {
            videoStream = stream;
            videoElement.srcObject = stream;
            videoElement.play();
            videoElement.onloadedmetadata = () => {
                scanQRCode();
            };
        })
        .catch(function (err) {
            console.error("Camera access denied:", err);
            qrOutput.innerHTML = "Unable to access camera.";
        });
}

function scanQRCode() {
    const canvas = document.createElement('canvas');
    const context = canvas.getContext('2d');
    const videoWidth = videoElement.videoWidth;
    const videoHeight = videoElement.videoHeight;

    function detectQRCode() {
        canvas.width = videoWidth;
        canvas.height = videoHeight;
        context.drawImage(videoElement, 0, 0, videoWidth, videoHeight);

        const imageData = context.getImageData(0, 0, videoWidth, videoHeight);
        const qrCode = jsQR(imageData.data, videoWidth, videoHeight);

        if (qrCode) {
            stopCamera();
            qrOutput.innerHTML = `QR Code detected: ${qrCode.data}`;
            console.log("QR Code Data:", qrCode.data);
            startScanButton.disabled = true;
        } else {
            requestAnimationFrame(detectQRCode);
        }
    }

    detectQRCode();
}

function stopCamera() {
    if (videoStream) {
        const tracks = videoStream.getTracks();
        tracks.forEach(track => track.stop());
    }
}

startScanButton.addEventListener('click', startCamera);
