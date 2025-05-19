export function createCameraPopup(currentOrderId, currentOrderDdsReference) {
    // Remove existing one if present
    const existing = document.getElementById("cameraDiv");
    if (existing) existing.remove();

    // Create outer popup div
    const cameraDiv = document.createElement("div");
    cameraDiv.id = "cameraDiv";
    cameraDiv.classList.add("camera-popup");

    // Inner content
    cameraDiv.innerHTML = `
        <div id="camera-popup-content">
            <div id="videoArea">
                <h3>Video output:</h3>
                <div id="qrOutput"></div>
                <video id="webcamVideo" autoplay></video>
            </div>
            <div id="camera-popup-actions">
                <button class="clean-button" id="startScanButton">Start Scan</button>
                <button class="clean-button" id="completeAttachmentButton" disabled>Complete</button>
            </div>
            <p id="attachmentError" style="color: red;"></p>
        </div>
    `;

    // Append to the wrapper
    const wrapper = document.getElementById("wrapper-side-popup");
    if (!wrapper) {
        console.error("Missing #wrapper-side-popup element.");
        return;
    }
    wrapper.appendChild(cameraDiv);

    // Get references inside the popup
    const videoElement = document.getElementById('webcamVideo');
    const startScanButton = document.getElementById('startScanButton');
    const completeButton = document.getElementById('completeAttachmentButton');
    const qrOutput = document.getElementById('qrOutput');
    const errorOutput = document.getElementById('attachmentError');
    const cancelButton = document.getElementById('closeBtn');

    let videoStream = null;
    let scannedDdsReference = null;
    let canvas = null;

    function startCamera() {
        console.log("Camera start requested");
        navigator.mediaDevices.getUserMedia({ video: true })
            .then((stream) => {
                videoStream = stream;
                videoElement.srcObject = stream;
                videoElement.play();
                videoElement.onloadedmetadata = () => {
                    scanQRCode(currentOrderDdsReference);
                };
            })
            .catch((err) => {
                console.error("Camera access denied:", err);
                qrOutput.textContent = "Unable to access camera.";
            });
    }

    function scanQRCode(currentOrderDdsReference) {
        canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        canvas.willReadFrequently = true;

        function detectQRCode() {
            const videoWidth = videoElement.videoWidth;
            const videoHeight = videoElement.videoHeight;

            if (videoWidth === 0 || videoHeight === 0) {
                requestAnimationFrame(detectQRCode);
                return;
            }

            canvas.width = videoWidth;
            canvas.height = videoHeight;
            context.drawImage(videoElement, 0, 0, videoWidth, videoHeight);

            const imageData = context.getImageData(0, 0, videoWidth, videoHeight);
            const qrCode = jsQR(imageData.data, videoWidth, videoHeight);

            if (qrCode) {
                console.log('QR Code detected:', qrCode.data);
                canvas.remove();
                stopCamera();
                scannedDdsReference = qrCode.data;
                qrOutput.textContent = `QR Code detected: ${scannedDdsReference}`;
                checkDDSReference(scannedDdsReference, currentOrderDdsReference);
            } else {
                requestAnimationFrame(detectQRCode);
            }
        }

        detectQRCode();
    }

    function checkDDSReference(scannedDds, currentOrderDdsReference) {
        if (scannedDds === currentOrderDdsReference) {
            startScanButton.disabled = true;
            completeButton.disabled = false;
            console.log("Scanned:", scannedDds);
            console.log("Expected:", currentOrderDdsReference);

            const cameraPopupCont = document.getElementById("camera-popup-content");
            const videoArea = document.getElementById('videoArea');
            const cameraPopupActions = document.getElementById('camera-popup-actions');
            cameraPopupCont.removeChild(videoArea);
            cameraPopupActions.removeChild(startScanButton);

            errorOutput.textContent = 'Successfully Recognised DDS Ref';
        } else {
            errorOutput.textContent = 'DDS reference mismatch. Please try again.';
        }
    }

    function stopCamera() {
        if (videoStream) {
            videoStream.getTracks().forEach(track => track.stop());
            videoStream = null;
        }
        videoElement.pause();
        videoElement.srcObject = null;
    }

    async function completeAttachment(qrData) {
        try {
            const response = await fetch(`/api/deliveries/attached/${currentOrderId}`, {
                method: 'POST',
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

    // Event listeners
    startScanButton.addEventListener('click', () => {
        startCamera();
        errorOutput.textContent = '';
        qrOutput.textContent = '';
        completeButton.disabled = true;
        startScanButton.disabled = true; // Optional: disable during scan
    });

    cancelButton.addEventListener('click',  () => {
        stopCamera();
    });

    completeButton.addEventListener('click', async () => {
        if (scannedDdsReference) {
            await completeAttachment(scannedDdsReference);
        } else {
            errorOutput.textContent = 'No QR code scanned yet.';
        }
    });
}
