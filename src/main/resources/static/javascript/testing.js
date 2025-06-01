import * as THREE from 'three';
import { PointerLockControls } from 'three/addons/controls/PointerLockControls.js';

// --- Scene setup ---
const scene = new THREE.Scene();
scene.background = new THREE.Color(0xaaaaaa);

// Crosshair creation - for aiming
const crosshair = document.createElement('div');
crosshair.style.position = 'fixed';
crosshair.style.top = '50%';
crosshair.style.left = '50%';
crosshair.style.width = '20px';
crosshair.style.height = '20px';
crosshair.style.marginLeft = '-10px';
crosshair.style.marginTop = '-10px';
crosshair.style.pointerEvents = 'none';
crosshair.style.zIndex = '10';
crosshair.style.borderLeft = '2px solid black';
crosshair.style.borderTop = '2px solid black';
document.body.appendChild(crosshair);

const camera = new THREE.PerspectiveCamera(75, window.innerWidth/window.innerHeight, 0.1, 1000);
camera.position.set(0, 2, 0); // Player eye height inside warehouse

const renderer = new THREE.WebGLRenderer({antialias:true});
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.shadowMap.enabled = true;
document.body.appendChild(renderer.domElement);

// --- Floor setup ---
const floorGeo = new THREE.PlaneGeometry(50, 50);
const floorMat = new THREE.MeshStandardMaterial({color: 0x666666});
const floor = new THREE.Mesh(floorGeo, floorMat);
floor.rotation.x = -Math.PI/2;
floor.receiveShadow = true;
scene.add(floor);

// --- Walls ---
const wallMat = new THREE.MeshStandardMaterial({color: 0x888888});
const wallThickness = 0.5;
const wallHeight = 10;
const roomSize = 50;

// Front wall
const frontWall = new THREE.Mesh(new THREE.BoxGeometry(roomSize, wallHeight, wallThickness), wallMat);
frontWall.position.set(0, wallHeight/2, -roomSize/2);
frontWall.receiveShadow = true;
scene.add(frontWall);
// Back wall
const backWall = frontWall.clone();
backWall.position.set(0, wallHeight/2, roomSize/2);
scene.add(backWall);
// Left wall
const leftWall = new THREE.Mesh(new THREE.BoxGeometry(wallThickness, wallHeight, roomSize), wallMat);
leftWall.position.set(-roomSize/2, wallHeight/2, 0);
scene.add(leftWall);
// Right wall
const rightWall = leftWall.clone();
rightWall.position.set(roomSize/2, wallHeight/2, 0);
scene.add(rightWall);

// --- Shelf creation parameters ---
const shelfWidth = 2;
const shelfDepth = 1;
const shelfHeight = 6;
const shelfLevels = 3;
const shelfSpacing = 4;  // space between shelves

// Create a shelf unit with multiple levels
function createShelf(x, z) {
    const group = new THREE.Group();

    const poleGeo = new THREE.CylinderGeometry(0.05, 0.05, shelfHeight, 8);
    const plankGeo = new THREE.BoxGeometry(shelfWidth, 0.1, shelfDepth);
    const poleMat = new THREE.MeshStandardMaterial({color: 0x7b4d2b});
    const plankMat = new THREE.MeshStandardMaterial({color: 0xdeb887});

    // Four vertical poles at shelf corners
    for(let i=0; i<4; i++){
        const pole = new THREE.Mesh(poleGeo, poleMat);
        pole.castShadow = true;
        pole.receiveShadow = true;
        pole.position.set(
            (i%2===0 ? -shelfWidth/2 : shelfWidth/2),
            shelfHeight/2,
            (i<2 ? shelfDepth/2 : -shelfDepth/2)
        );
        group.add(pole);
    }

    // Horizontal planks (levels)
    for(let level=0; level<shelfLevels; level++){
        const plank = new THREE.Mesh(plankGeo, plankMat);
        plank.position.set(0, 0.3 + level * (shelfHeight/(shelfLevels)), 0);
        plank.castShadow = true;
        plank.receiveShadow = true;
        group.add(plank);
    }

    group.position.set(x, 0, z);
    const ceilingPlank = new THREE.Mesh(plankGeo, plankMat);
    ceilingPlank.position.set(0, shelfHeight - 0.05, 0);
    ceilingPlank.castShadow = true;
    ceilingPlank.receiveShadow = true;
    group.add(ceilingPlank);

    return group;
}

// Create multiple shelves in rows and columns
const shelvesGroup = new THREE.Group();
const rows = 3;
const cols = 4;
for(let r=0; r<rows; r++){
    for(let c=0; c<cols; c++){
        const shelf = createShelf(
            r * shelfSpacing - ((rows-1)*shelfSpacing)/2,
            c * (shelfDepth + 2) - ((cols-1)*(shelfDepth + 2))/2
        );
        shelvesGroup.add(shelf);
    }
}
scene.add(shelvesGroup);

// --- Products on shelves ---
const productColors = [0xff4444, 0x44ff44, 0x4444ff, 0xffcc44];
const productSize = 0.3;

function createProduct(x,y,z,color){
    const geo = new THREE.BoxGeometry(productSize, productSize, productSize);
    const mat = new THREE.MeshStandardMaterial({color});
    const mesh = new THREE.Mesh(geo, mat);
    mesh.castShadow = true;
    mesh.position.set(x,y,z);

    // Add custom property to identify products later
    mesh.userData.isProduct = true;

    return mesh;
}

// Add products randomly on shelves
shelvesGroup.children.forEach(shelf => {
    for(let level=0; level<shelfLevels; level++){
        const productsCount = Math.floor(Math.random()*3) + 1;
        for(let p=0; p<productsCount; p++){
            const x = (Math.random()-0.5) * (shelfWidth - 0.5);
            const y = 0.3 + level * (shelfHeight/shelfLevels) + productSize/2 + 0.05;
            const z = (Math.random()-0.5) * (shelfDepth - 0.5);
            const color = productColors[Math.floor(Math.random()*productColors.length)];
            const product = createProduct(x, y, z, color);
            shelf.add(product);
        }
    }
});

// --- Lights ---
const ambient = new THREE.AmbientLight(0xffffff, 0.3);
scene.add(ambient);

const dirLight = new THREE.DirectionalLight(0xffffff, 1);
dirLight.position.set(10, 20, 10);
dirLight.castShadow = true;
dirLight.shadow.mapSize.width = 2048;
dirLight.shadow.mapSize.height = 2048;
dirLight.shadow.camera.near = 1;
dirLight.shadow.camera.far = 50;
dirLight.shadow.camera.left = -20;
dirLight.shadow.camera.right = 20;
dirLight.shadow.camera.top = 20;
dirLight.shadow.camera.bottom = -20;
scene.add(dirLight);

// --- Controls setup ---
const controls = new PointerLockControls(camera, renderer.domElement);

document.body.addEventListener('click', () => {
    controls.lock();
}, false);

// Movement variables and handlers
const move = { forward:false, backward:false, left:false, right:false };
const velocity = new THREE.Vector3();
const direction = new THREE.Vector3();

const onKeyDown = (e) => {
    switch(e.code){
        case 'ArrowUp':
        case 'KeyW': move.forward = true; break;
        case 'ArrowDown':
        case 'KeyS': move.backward = true; break;
        case 'ArrowLeft':
        case 'KeyA': move.left = true; break;
        case 'ArrowRight':
        case 'KeyD': move.right = true; break;
    }
};
const onKeyUp = (e) => {
    switch(e.code){
        case 'ArrowUp':
        case 'KeyW': move.forward = false; break;
        case 'ArrowDown':
        case 'KeyS': move.backward = false; break;
        case 'ArrowLeft':
        case 'KeyA': move.left = false; break;
        case 'ArrowRight':
        case 'KeyD': move.right = false; break;
    }
};

document.addEventListener('keydown', onKeyDown);
document.addEventListener('keyup', onKeyUp);

// --- Raycaster and mouse vector for clicking products ---
const raycaster = new THREE.Raycaster();
const mouse = new THREE.Vector2(); // Not used for mouse position, but for center screen raycasting

// Log for changes and intentions - helps track the code's evolution and purpose
console.log('[Init] Added raycaster for detecting product clicks');

// Function to open product menu (assuming you have an existing HTML menu with id 'productMenu')
function openProductMenu(product){
    // Log which product is clicked (can be extended to show product info)
    console.log(`[Product Clicked] Product at position (${product.position.x.toFixed(2)}, ${product.position.y.toFixed(2)}, ${product.position.z.toFixed(2)}) clicked.`);

    const menu = document.getElementById('productMenu');
    if(menu){
        menu.style.display = 'block';
        // Optionally populate menu content based on product - extend here as needed
    } else {
        console.warn('[Menu] No HTML element with id "productMenu" found.');
    }
}

// Detect product click using pointer lock controls and mouse click event
document.addEventListener('mousedown', (event) => {
    // Only trigger when controls are locked (i.e. in game mode)
    if(!controls.isLocked) return;

    // Raycasting from camera center (crosshair position)
    // Normalized device coordinates for center screen: x=0, y=0
    mouse.x = 0;
    mouse.y = 0;

    raycaster.setFromCamera(mouse, camera);

    // Intersect all objects in the scene - specifically products
    const intersects = raycaster.intersectObjects(shelvesGroup.children.flatMap(shelf => shelf.children), true);

    if(intersects.length > 0){
        // Find first intersected product by checking userData flag
        const productIntersect = intersects.find(i => i.object.userData.isProduct);
        if(productIntersect){
            openProductMenu(productIntersect.object);
        }
    }
}, false);

// --- Animation loop ---
const clock = new THREE.Clock();

function animate(){
    requestAnimationFrame(animate);

    const delta = clock.getDelta();
    const speed = 40;

    // Damping velocity for smooth movement
    velocity.x -= velocity.x * 10 * delta;
    velocity.z -= velocity.z * 10 * delta;

    direction.z = Number(move.forward) - Number(move.backward);
    direction.x = Number(move.right) - Number(move.left);
    direction.normalize();

    if (move.forward || move.backward) velocity.z -= direction.z * speed * delta;
    if (move.left || move.right) velocity.x -= direction.x * speed * delta;

    controls.moveRight(-velocity.x * delta);
    controls.moveForward(-velocity.z * delta);

    renderer.render(scene, camera);
}

animate();

window.addEventListener('resize', () => {
    camera.aspect = window.innerWidth/window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
});
