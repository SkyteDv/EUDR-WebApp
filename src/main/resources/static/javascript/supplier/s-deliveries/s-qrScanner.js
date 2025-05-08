document.addEventListener('DOMContentLoaded', () => {
    const videoElement = document.getElementById('webcamVideo');
    const startScanButton = document.getElementById('startScanButton');
    const qrOutput = document.getElementById('qrOutput');
    const errorOutput = document.getElementById('attachmentError');
    const cancelButton = document.getElementById('cancelButton');

    let videoStream = null;
    let scannedDdsReference = null;
    let canvas = null;

    // Function to start the camera and initiate QR code scanning
    function startCamera() {
        console.log("started process");
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(function (stream) {
                videoStream = stream;
                videoElement.srcObject = stream;
                videoElement.play();
                videoElement.onloadedmetadata = () => {
                    scanQRCode(); // Start scanning as soon as metadata is loaded
                };
            })
            .catch(function (err) {
                console.error("Camera access denied:", err);
                qrOutput.innerHTML = "Unable to access camera.";
            });
    }

    function scanQRCode() {
        canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        canvas.willReadFrequently = true;

        function detectQRCode() {
            const videoWidth = videoElement.videoWidth;
            const videoHeight = videoElement.videoHeight;

            // Ensure the video dimensions are available
            if (videoWidth === 0 || videoHeight === 0) {
                requestAnimationFrame(detectQRCode); // Try again
                return;
            }

            canvas.width = videoWidth;
            canvas.height = videoHeight;
            context.drawImage(videoElement, 0, 0, videoWidth, videoHeight);

            const imageData = context.getImageData(0, 0, videoWidth, videoHeight);
            const qrCode = jsQR(imageData.data, videoWidth, videoHeight);

            if (qrCode) {
                console.log('qrCode', qrCode.data);
                canvas.remove();
                stopCamera();
                const qrData = qrCode.data;
                qrOutput.innerHTML = `QR Code detected: ${qrData}`;
                scannedDdsReference = qrData;
                checkDDSReference(scannedDdsReference);
            } else {
                requestAnimationFrame(detectQRCode); // Keep scanning
            }
        }

        detectQRCode();
    }


    function getCurrentOrderDdsReference() {
        return document.getElementById('currentOrderDdsReference').value;
    }

    function checkDDSReference(scannedDds) {
        const currentOrderDdsReference = getCurrentOrderDdsReference();

        if (scannedDds === currentOrderDdsReference) {
            const completeBtn = document.getElementById('completeAttachmentButton');
            startScanButton.disabled = true;
            completeBtn.disabled = false;
            errorOutput.textContent = '';
        } else {
            errorOutput.textContent = 'DDS reference mismatch. Please try again.';
        }
    }

    function stopCamera() {
        if (videoStream) {
            const tracks = videoStream.getTracks();
            tracks.forEach(track => track.stop());
            videoStream = null;
        }

        videoElement.pause();
        videoElement.srcObject = null;
    }


    startScanButton.addEventListener('click', startCamera);
    cancelButton.addEventListener('click', () => {
        stopCamera();
    });
    document.getElementById('completeAttachmentButton').addEventListener('click', async () => {
        const errorOutput = document.getElementById('attachmentError');

        if (scannedDdsReference) {
            await completeAttachment(scannedDdsReference, errorOutput);
        } else {
            errorOutput.textContent = 'No QR code scanned yet.';
        }
    });

    async function completeAttachment(qrData, errorOutput) {
        try {
            const response = await fetch(`/api/deliveries/attached/${qrData}`, {
                method: 'POST'
            });

            if (response.ok) {
                window.location.href = '/supplier/deliveries';
            } else {
                errorOutput.textContent = 'Failed to complete attachment. Please try again.';
            }
        } catch (err) {
            console.error('Request failed:', err);
            errorOutput.textContent = 'An error occurred while connecting to the server.';
        }
    }
});
