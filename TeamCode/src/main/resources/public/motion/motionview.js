let monitoring = "";
let currentMotion = {};

const cavnasDOM = document.querySelector("#motion-canvas");
const ctx = cavnasDOM.getContext("2d");

/** REST API ENDPOINTS **/
async function getProfiles() {
    const req = await fetch("/api/motion/get/profiles");

    return (await req.json())["profiles"];
}

async function getFirstProfile() {
    const req = await fetch("/api/motion/get");

    return await req.json();
}

async function getProfileByName(name) {
    const req = await fetch("/api/motion/get/" + name);

    return await req.json();
}

let motionUpdate = {
    translation: {
        x: 0, //m/s
        y: 0 //m/s
    },
    rotation_speed: 0, //degrees
    wheel_motions: [
        {
            direction: 0, //degrees
            power: 0 //m/s?
        }
    ],
    wheel_number: 0
};

function draw() {
    ctx.clearRect(0, 0, cavnasDOM.width, cavnasDOM.height);

    if (currentMotion["name"] != null) {
        ctx.font = "30px Arial";
        ctx.fillStyle = "black";
        ctx.fillText(currentMotion["name"], 10, 50);
    }

    ctx.fillStyle = "black";
    ctx.fillRect(
        125, 125, 50, 50
    );

    //draw an arrow left and up
    ctx.beginPath();
    ctx.moveTo(125, 150);
    ctx.lineTo(125 - ((motionUpdate.translation.x / 2) * 75), 150);
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(150, 125);
    ctx.lineTo(150, 125 - ((motionUpdate.translation.y / 2) * 75));
    ctx.fill();

    //TODO: once this starts working on the live robot, add in a rotation in the top right corner
}

function setupRESTUpdating() {
    if (motionUpdate != null) clearInterval(motionUpdate);

    motionUpdate = setInterval(async () => {
        const profiles = await getProfiles();
        if (profiles.length > 1) {
            if (monitoring.length > 0) {
                currentMotion = await getProfileByName(monitoring)
            } else {
                currentMotion = await getFirstProfile();
                monitoring = currentMotion["name"];
            }
        }
    }, 500);
}

function setupSocketUpdating() {
    //TODO
}

setupRESTUpdating();

setInterval()