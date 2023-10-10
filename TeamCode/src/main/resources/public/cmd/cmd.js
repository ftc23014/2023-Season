let ip = "localhost";

function clearInput() {
    addCMDToOutput(document.querySelector("#cmd-input").value);
    document.querySelector("#cmd-input").value = "";
}

function addCMDToOutput(cmd) {
    document.querySelector("#cmd-output").innerHTML += ip + ":~ robot$ " + cmd + "<br>";
}

function loadLocalStorage() {
    if (localStorage.getItem("ip") !== null) {
        ip = localStorage.getItem("ip");
    }
}

function saveToLocalStorage() {
    localStorage.setItem("ip", ip);
}

async function send() {
    const cmd = document.querySelector("#cmd-input").value;

    if (cmd === "") {
        return "";
    }

    if (cmd === "clear") {
        clearInput();
        document.querySelector("#cmd-output").innerHTML = "";
        return "";
    }

    if (cmd.startsWith("set-ip")) {
        const newIp = cmd.split(" ")[1];
        if (newIp === undefined) {
            return "IP was not specified";
        }
        ip = newIp;
        saveToLocalStorage();
        clearInput();
        return "Successfully set remote IP to " + ip;
    }

    clearInput();

    const req = await fetch("http://" + ip + ":8000" + "/api/cmd", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            // set request mode to no-cors
            
        },
        body: JSON.stringify({
            command: cmd
        })
    })

    //response will be a string
    const res = await req.text();

    return res;
}

//on enter, send command to server
document.querySelector("#cmd-input").addEventListener("keyup",
    async (event) => {
        if (event.keyCode === 13) {
            event.preventDefault();
            const resp = await send();

            if (resp !== "") {
                document.querySelector("#cmd-output").innerHTML += resp.replaceAll("\n", "<br>") + "<br>";
            }
        }
    }
)

loadLocalStorage();