
const driverstationDOM = document.querySelector(".driverstation");
let activeDOM;

const GAMEMODE = {
    AUTONOMOUS: 0,
    TELEOP: 1,
}

async function getGamemode() {
    let response = await fetch("/api/simulation/gamemode");
    //plain text
    let gamemode = await response.text();
    return gamemode;
}

async function enterGamemode(gamemode) {
    let response = await fetch("/api/simulation/gamemode/enter", {
        method: "POST",
        body: gamemode === GAMEMODE.AUTONOMOUS ? "autonomous" : "teleop"
    });

    if (response.status !== 200) {
        console.error("Failed to enter gamemode");
    }
}

async function exitGamemode(gamemode) {
    let response = await fetch("/api/simulation/gamemode/exit", {
        method: "POST",
    });

    if (response.status !== 200) {
        console.error("Failed to exit gamemode");
    }
}

async function onLoad() {
    let rown = 0;
    for (let row of driverstationDOM.children[0].children) {
        let i = 0;
        for (let cell of row.children) {
            if (i === 0) {
                i++;
                continue;
            }

            let exit = i === 1;
            let gamemode = rown === 1 ? GAMEMODE.AUTONOMOUS : GAMEMODE.TELEOP;
            const cellCopy = cell;

            cell.addEventListener("click", () => {
                if (activeDOM) activeDOM.classList.toggle("active-button");

                cellCopy.classList.toggle("active-button");
                activeDOM = cellCopy;

                if (document.getElementsByClassName("active-button").length > 1) {
                    console.error("More than one active button??? weird bruh");

                    for (let button of document.getElementsByClassName("active-button")) {
                        if (button !== cellCopy) {
                            button.classList.toggle("active-button");
                        }
                    }
                }

                if (!exit) {
                    enterGamemode(gamemode);
                } else {
                    exitGamemode(gamemode);
                }
            });

            i++;
        }
        rown++;
    }

    activeDOM = driverstationDOM.children[0].children[1].children[1];

    const current_gamemode = await getGamemode();

    if (current_gamemode !== "DISABLED") {
        document.querySelector(".active-button").classList.toggle("active-button");
        activeDOM = null;
    }

    if (current_gamemode === "AUTONOMOUS") {
        driverstationDOM.children[0].children[1].children[2].classList.toggle("active-button");
        activeDOM = driverstationDOM.children[0].children[1].children[2];
    } else if (current_gamemode === "TELEOP") {
        driverstationDOM.children[0].children[0].children[2].classList.toggle("active-button");
        activeDOM = driverstationDOM.children[0].children[0].children[2];
    }
}

onLoad();