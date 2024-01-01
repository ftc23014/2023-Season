const emojis = {
    disconnected: "🟥",
    connected: "🟩"
}

let ip = Object.keys(window.localStorage).includes("ip") ? window.localStorage.ip : "localhost";

document.getElementById("source").innerText = (ip === "localhost" ? " :// " : ip);

let dataLimit = 0; //the farthest the log goes back
let lastDataRequest = 0; //when the last data was requested

let localCache = []; //keep track of the logs here

let connected = false;

async function requestFullLog() {
    const req = await fetch(`http://${ip}:8000/api/log`);

    if (req.status === 404) {
        document.getElementById("active").textContent = emojis.disconnected;
    } else {
        document.getElementById("active").textContent = emojis.connected;
    }

    const json = await req.json();

    if (req.ok) {
        localCache = [];
        dataLimit = json.last_clear;
        for (let msg of json.log) {
            localCache.push({
                msg: msg.msg,
                time: new Date(msg.time)
            })
        }

        connected = true;

        lastDataRequest = Date.now();

        formatLog();
    }
}

async function requestAfterTime(timestamp=0) {
    const req = await fetch(`http://${ip}:8000/api/log`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            timestamp
        })
    });

    if (req.status === 404) {
        document.getElementById("active").textContent = emojis.disconnected;
    } else {
        document.getElementById("active").textContent = emojis.connected;
    }

    const json = await req.json();

    if (req.ok) {
        for (let msg of json.log) {
            localCache.push({
                msg: msg.msg,
                time: new Date(msg.time)
            })
        }

        connected = true;

        lastDataRequest = Date.now();

        formatLog();
    }
}

function formatLog() {
    document.getElementById("logs").innerHTML = "";

    let initial = document.createElement("p");
    initial.textContent = `-- Robot Log [after ${new Date(dataLimit).toISOString()} - last updated at ${new Date(lastDataRequest).toISOString()}] --`
    document.getElementById("logs").appendChild(initial);

    for (let l of localCache) {
        let p = document.createElement("p");
        p.textContent = l.msg;

        document.getElementById("logs").appendChild(p);
    }
}

function editIP() {
    let newIP = prompt("what's the ip of the robot / whatever you're tracking?");

    if (!newIP) return;

    if (newIP === "://" || newIP === "::") {
        newIP = "localhost";
    }

    window.localStorage.setItem("ip", newIP);

    ip = newIP;
    document.getElementById("source").innerText = (ip === "localhost" ? " :// " : ip);
}

setInterval(async () => {
    try {
        if (!connected) {
            await requestFullLog();
        } else {
            await requestAfterTime(lastDataRequest + 1);
        }
    } catch (e) {
        connected = false;
        document.getElementById("active").textContent = emojis.disconnected;
    }
}, 1000)