
const fieldCanvasDOM = document.getElementById("field");
const fieldCanvas = fieldCanvasDOM.getContext("2d");

function draw() {
    drawCenterStage(fieldCanvas);

    requestAnimationFrame(draw);
}

centerStageInit();

draw();