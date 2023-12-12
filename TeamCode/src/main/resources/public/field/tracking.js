const updateInterval = 1000;

let ip = Object.keys(window.localStorage).includes("ip") ? window.localStorage.ip : "localhost";

let robot_position = {
    x: new Unit(0, Unit.Type.INCHES),
    y: new Unit(0, Unit.Type.INCHES),
    angle: 0
};

async function updatePosition() {
    const request = await fetch(`https://${ip}:8000/api/position`);

    const response = await request.json();

    robot_position.x = new Unit(response.x, Unit.Type.METERS);
    robot_position.y = new Unit(response.y, Unit.Type.METERS);
    robot_position.angle = (response.angle / 180) * Math.PI;
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
}

setInterval(updatePosition, updateInterval)