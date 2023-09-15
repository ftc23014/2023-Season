
const fieldCanvasDOM = document.getElementById("field");
const fieldCanvas = fieldCanvasDOM.getContext("2d");

let currentPath = new Path();

const CURRENT_FIELD = CENTER_STAGE;
const CURRENT_FIELD_STANDARDS = CENTER_STAGE_STANDARDS;

let mousePos = {
    x: 0,
    y: 0,
    down: false
}

function draw() {
    const ctx = fieldCanvas;

    drawCenterStage(ctx);

    currentPath.displayCurves(ctx);
    currentPath.displayWaypoints(ctx, mousePos);

    requestAnimationFrame(draw);
}

//mouse events
document.addEventListener("mousemove", (e) => {
    mousePos.x = e.clientX - fieldCanvasDOM.offsetLeft;
    mousePos.y = e.clientY - fieldCanvasDOM.offsetTop;
});

document.addEventListener("mousedown", (e) => {
    mousePos.down = true;
});

document.addEventListener("mouseup", (e) => {
    mousePos.down = false;
});

fieldCanvasDOM.addEventListener("mouseleave", (e) => {
    mousePos.down = false;
});

currentPath.updateList();

centerStageInit();

draw();