
setTimeout(() => {
    document.getElementById("editing").innerText = `
number detection = 0

run "huskylens" {lambda(detected)}
: set detection to detected

if detection == 0
: run parallel
:: run "linearslides"
:: run instant
::: print "hello world!"
::: print "i'm moving"`;
    update(document.getElementById("editing").value)
}, 1000000)

function update(text) {
    let result_element = document.querySelector("#highlighting-content");
    // Handle final newlines (see article)
    if(text[text.length-1] == "\n") {
        text += " ";
    }
    // Update code
    result_element.innerHTML = text.replace(new RegExp("&", "g"), "&amp;").replace(new RegExp("<", "g"), "&lt;"); /* Global RegExp */

    document.getElementById("editor-numbers").innerHTML = "";

    for (let i = 0; i < text.split("\n").length; i++) {
        let n = document.createElement("span");

        n.textContent = (i + 1).toString();

        document.getElementById("editor-numbers").appendChild(n);
    }

    highlight();
}

let generator = (l) => {
    const st = `私下手王寮穴人口中々風間川一二三四五六七八九十上下`.split("");
    let a = "";
    for (let i = 0; i < l; i++) {
        a += st[Math.floor(Math.random() * st.length)];
    }

    return a;
}

function highlight() {
    let textContent = document.getElementById("highlighting-content").innerHTML;

    let lines = textContent.split("\n");

    const datatypes = [
        "boolean",
        "number",
        "string"
    ]

    const built_in_commands = [
        "parallel",
        "sequential",
    ]

    for (let i = 0; i < lines.length; i++) {
        let line = lines[i];

        if (line.startsWith("//")) { //comments
            lines[i] = `<span class="token comment">${line}</span>`

            continue;
        }

        let colonCount = 0;

        if (line.startsWith(":")) {
            while (line.split(" ")[0].startsWith(":")) {
                colonCount++;
                let m = line.split("");
                m.shift();
                line = m.join("");
            }
            lines[i] = line;
        }

        //beginnings

        if (line.trim().startsWith("run")) { //running commands
            lines[i] = line.replace("run", `あ`)
        } else if (datatypes.includes(line.split(" ")[0])) { //variables
            lines[i] = line.replace(line.split(" ")[0], `び`)
        } else if (line.trim().startsWith("set")) { //setting variables
            lines[i] = line.replace("set", `す`);
        } else if (line.trim().startsWith("if")) { //setting variables
            lines[i] = line.replace("if", `で`);
        } else if (line.trim().startsWith("print")) { //print statements
            lines[i] = line.replace("print", `え`);
        } else if (line.trim().startsWith("update")) { //updating for print to telemetry or console
            lines[i] = line.replace("update", `ふ`);
        }


        //intermediataries

        let qot = [];

        if (line.includes("\"")) {
            let splt = lines[i].split("\"");

            for (let i = 0; i < splt.length; i++) {
                if (i % 2 === 1) {
                    const gn = generator(6);
                    qot.push({
                        key: gn,
                        content: splt[i],
                        last_in_str: i === splt.length - 1
                    });

                    splt[i] = gn;
                }
            }

            lines[i] = splt.join("\"");
        }

        let numbers = [];

        let linesplt = lines[i].split(/\.| |=|\{|}|,/i);
        let indexThru = 0;
        let RLINDX = 0;

        let joiners = [];

        for (let j = 0; j < linesplt.length; j++) {
            let add = linesplt[j].length + 1;

            if (/^\d+$/.test(linesplt[j])) {
                let w = generator(6);
                numbers.push({
                    n: linesplt[j],
                    key: w,
                    before: lines[i][indexThru - 1],
                    after: indexThru + linesplt[j].length < lines[i].length ? lines[i][indexThru + linesplt[j].length] : ""
                })

                linesplt[j] = w;
            }

            if (j > 0) joiners.push(lines[i][indexThru - 1]);

            indexThru += add;
        }

        let s = linesplt[0];

        for (let j = 1; j < linesplt.length; j++) {
            s += joiners[j - 1] + linesplt[j];
        }

        lines[i] = s;


        lines[i] = lines[i].replace(`あ`, `<span class="token important">run</span>`);
        lines[i] = lines[i].replace(`び`, `<span class="token symbol">${line.split(" ")[0]}</span>`)
        lines[i] = lines[i].replace(`す`,`<span class="token important">set</span>`);
        lines[i] = lines[i].replace(`で`, `<span class="token important">if</span>`);
        lines[i] = lines[i].replace(`え`, `<span class="token important">print</span>`);
        lines[i] = lines[i].replace(`ふ`, `<span class="token important">update</span>`)

        lines[i] = lines[i].replaceAll(" to ", ` <span class="token important">to</span> `);
        lines[i] = lines[i].replaceAll("lambda(", `<span class="token symbol">lambda</span>(`)

        for (let q of qot) {
            lines[i] = lines[i].substring(0, lines[i].indexOf(q.key) - 1) + `<span class="token string">"${q.content}${q.last_in_str ? "" : `"`}</span>` +
                (

                   q.last_in_str ? "" : lines[i].substring(lines[i].indexOf(q.key) + q.key.length + 1, lines[i].length)
                );
        }

        let lastPeriod = -1;
        for (let num of numbers) {
            let indxOf = lines[i].indexOf(num.key);

            let splt =  lines[i].split("");
            splt[indxOf - 1] = num.before;

            if (indxOf - 1 === lastPeriod) {
                splt[indxOf - 1] = `<span class="token number">.</span>`
            }

            if (num.after != null && num.after.length > 0) {
                splt[indxOf + num.key.length] = num.after;
            }

            let rplc = `<span class="token number">${num.n}</span>`;

            lines[i] = splt.join("").replace(num.key, rplc)

            if (num.after == ".") {
                lastPeriod = indxOf + rplc.length;
            }
        }

        let colonSTR = "";
        for (let j = 0; j < colonCount; j++) {
            colonSTR += ":"
        }

        lines[i] = `<span class="token punctuation">${colonSTR}</span>` + lines[i]
    }

    document.getElementById("highlighting-content").innerHTML = lines.join("\n");
}

function sync_scroll(element) {
    /* Scroll result to scroll coords of event - sync with textarea */
    let result_element = document.querySelector("#highlighting");
    // Get and set x and y
    result_element.scrollTop = element.scrollTop;
    result_element.scrollLeft = element.scrollLeft;

    document.getElementById("editor-numbers").scrollTop = element.scrollTop;
}

function check_tab(element, event) {
    let code = element.value;
    if(event.key == "Tab") {
        /* Tab key pressed */
        event.preventDefault(); // stop normal
        let before_tab = code.slice(0, element.selectionStart); // text before tab
        let after_tab = code.slice(element.selectionEnd, element.value.length); // text after tab
        let cursor_pos = element.selectionStart + 1; // where cursor moves after tab - moving forward by 1 char to after tab
        element.value = before_tab + "\t" + after_tab; // add tab char
        // move cursor
        element.selectionStart = cursor_pos;
        element.selectionEnd = cursor_pos;
        update(element.value); // Update text to include indent
    }
}

