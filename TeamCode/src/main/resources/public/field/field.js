
const fieldCanvasDOM = document.getElementById("field");
const fieldCanvas = fieldCanvasDOM.getContext("2d");

let currentPath = new ConstructedBezierPath();

const CURRENT_FIELD = CENTER_STAGE;
const CURRENT_FIELD_STANDARDS = CENTER_STAGE_STANDARDS;

let ROBOT_SIZE = {
    w: new Unit(18, Unit.Type.INCHES),
    h: new Unit(18, Unit.Type.INCHES)
};

let mousePos = {
    x: 0,
    y: 0,
    down: false
}

let show = {
    robot_positions: true,
    control_points: true,
}

function draw() {
    const ctx = fieldCanvas;

    drawCenterStage(ctx);

    currentPath.displayCurves(ctx);
    currentPath.displayWaypoints(ctx, mousePos);
    currentPath.displayRobotStates(ctx);

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

document.addEventListener("keyup", (e) => {
    if (e.key == "f") {
        show.robot_positions = !show.robot_positions;
    } else if (e.key == "g") {
        show.control_points = !show.control_points;
    }
});

currentPath.updateList();

centerStageInit();

draw();