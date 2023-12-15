const updateInterval = 1000;

let ip = Object.keys(window.localStorage).includes("ip") ? window.localStorage.ip : "localhost";

let robot_position = {
    x: new Unit(0, Unit.Type.INCHES),
    y: new Unit(0, Unit.Type.INCHES),
    angle: 0
};

let initialPos = {
    x: new Unit(9.5, Unit.Type.INCHES),
    y: new Unit(104.5, Unit.Type.INCHES)
};

async function updatePosition() {
    const request = await fetch(`http://${ip}:8000/api/position`);

    const response = await request.json();

    robot_position.x = new Unit(-response.x + initialPos.x.get(Unit.Type.METERS), Unit.Type.METERS);
    robot_position.y = new Unit(-response.y + initialPos.y.get(Unit.Type.METERS), Unit.Type.METERS);
    robot_position.angle = (-((360 - response.rotation)) / 180) * Math.PI;
}

function drawRobotCurrentPosition(ctx) {
    ctx.save();

    ctx.beginPath();

    ctx.strokeStyle = "#ffffff";

    //move to the center of the robot
    ctx.translate(
        robot_position.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
        robot_position.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
    );

    ctx.rotate(-robot_position.angle);

    ctx.rect(
        -(ROBOT_SIZE.w.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) / 2),
        -(ROBOT_SIZE.h.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) / 2),
        ROBOT_SIZE.w.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
        ROBOT_SIZE.h.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
    );
    ctx.stroke();

    ctx.restore();
}

setInterval(updatePosition, updateInterval)