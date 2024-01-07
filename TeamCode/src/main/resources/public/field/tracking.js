const updateInterval = 1000;

let ip = Object.keys(window.localStorage).includes("ip") ? window.localStorage.ip : "localhost";

let robot_position = {
    x: new Unit(0, Unit.Type.INCHES),
    y: new Unit(0, Unit.Type.INCHES),
    angle: 0
};

let initialPos = {
    x: new Unit(0, Unit.Type.INCHES),
    y: new Unit(0, Unit.Type.INCHES)
};


let vision_pose = {
    x: new Unit(0, Unit.Type.INCHES),
    y: new Unit(0, Unit.Type.INCHES),
    angle: 0
}

let aprilTags = [];

async function updatePosition() {
    const request = await fetch(`http://${ip}:8000/api/position`);

    const response = await request.json();

    robot_position.x = new Unit(-response.x + initialPos.x.get(Unit.Type.METERS), Unit.Type.METERS);
    robot_position.y = new Unit(-response.y + initialPos.y.get(Unit.Type.METERS), Unit.Type.METERS);
    robot_position.angle = (-((360 - response.rotation)) / 180) * Math.PI;


    let req2 = null;
    try {
        req2 = await fetch(`http://${ip}:8000/api/vision_pose`).catch((e) => {});
    } catch(e) {
        return;
    }

    let res2 = null;
    try {
        res2 = await req2.json();
    } catch(e) {
        return;
    }

    vision_pose.x = new Unit(res2.x, Unit.Type.METERS);
    vision_pose.y = new Unit(res2.y, Unit.Type.METERS);
    vision_pose.angle = (-((360 - res2.rotation)) / 180) * Math.PI;

    aprilTags = [];

    for (let aprilTag of res2.detections) {
        aprilTags.push({
            x: new Unit(aprilTag.x, Unit.Type.METERS),
            y: new Unit(aprilTag.y, Unit.Type.METERS),
            real_x: new Unit(aprilTag.real_x, Unit.Type.METERS),
            real_y: new Unit(aprilTag.real_y, Unit.Type.METERS),
            angle: (-((360 - aprilTag.yaw)) / 180) * Math.PI,
            id: aprilTag.id
        });
    }
}

function drawAprilTags(ctx) {
    for (let aprilTag of aprilTags) {
        ctx.strokeStyle = "#e5b035";
        ctx.rect(
            aprilTag.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) - 5,
            aprilTag.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) - 5,
            10,
            10
        );

        ctx.stroke();

        ctx.strokeStyle = "#9113c9";

        ctx.rect(
            aprilTag.real_x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) - 5,
            aprilTag.real_y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) - 5,
            10,
            10
        );

        ctx.stroke();
    }
}

function drawRobotCurrentPosition(ctx) {
    drawAprilTags(ctx);

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

    //draw vision position
    ctx.save();

    ctx.beginPath();

    ctx.strokeStyle = "#2abb24";

    //move to the center of the robot
    ctx.translate(
        vision_pose.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
        vision_pose.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
    );

    //ctx.rotate(-vision_pose.angle);

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