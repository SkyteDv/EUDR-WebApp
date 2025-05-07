document.addEventListener('DOMContentLoaded', () => {
    const videoElement = document.getElementById('webcamVideo');
    const startScanButton = document.getElementById('startScanButton');
    const qrOutput = document.getElementById('qrOutput');
    const errorOutput = document.getElementById('attachmentError');
    const cancelButton = document.getElementById('cancelButton');

    let videoStream = null;
    let scannedDdsReference = null; // Store scanned DDS reference

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

    // Function to scan the QR code and verify DDS reference
    function scanQRCode() {
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');

        canvas.willReadFrequently = true;

        const videoWidth = videoElement.videoWidth;
        const videoHeight = videoElement.videoHeight;

        function detectQRCode() {
            canvas.width = videoWidth;
            canvas.height = videoHeight;
            context.drawImage(videoElement, 0, 0, videoWidth, videoHeight);

            const imageData = context.getImageData(0, 0, videoWidth, videoHeight);
            const qrCode = jsQR(imageData.data, videoWidth, videoHeight);

            if (qrCode) {
                console.log('qrCode', qrCode.data);
                stopCamera(); // Stop the camera once QR code is detected
                const qrData = qrCode.data; // Get the scanned DDS reference
                qrOutput.innerHTML = `QR Code detected: ${qrData}`;
                scannedDdsReference = qrData; // Store the scanned DDS reference

                // Check if the scanned DDS reference matches the current order's DDS reference
                checkDDSReference(scannedDdsReference);
            } else {
                requestAnimationFrame(detectQRCode);
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

    // Function to stop the camera
    function stopCamera() {
        if (videoStream) {
            const tracks = videoStream.getTracks();
            tracks.forEach(track => track.stop());
        }
    }

    startScanButton.addEventListener('click', startCamera);
    cancelButton.addEventListener('click', stopCamera);

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
