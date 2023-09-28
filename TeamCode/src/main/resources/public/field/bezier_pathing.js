
class ConstructedBezierPath {
    constructor() {
        this.waypoints = [];
        this.curves = []; //list of BezierPath objects

        this.ui = {
            selectedWaypoint: null,
            lastSelectedWaypoint: null,
            rotating: false,
            rotatingWaypoint: null
        }
    }
    displayRobotStates(ctx=new CanvasRenderingContext2D()) {
        for (let hardPoint of this.getWaypoints(WaypointType.Hard)) {
            const angle = hardPoint.heading.get(Angle.Radians);

            ctx.save();

            ctx.beginPath();

            ctx.strokeStyle = "#ffffff";

            //move to the center of the robot
            ctx.translate(
                hardPoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                hardPoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
            );

            ctx.rotate(-angle);

            ctx.rect(
                -(ROBOT_SIZE.w.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) / 2),
                -(ROBOT_SIZE.h.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) / 2),
                ROBOT_SIZE.w.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                ROBOT_SIZE.h.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
            );
            ctx.stroke();

            //counterclockwise, with facing the right being 0°
            ctx.beginPath();
            ctx.arc(
                ROBOT_SIZE.w.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) / 2,
                0,
                5,
                0,
                2 * Math.PI
            );

            ctx.fillStyle = "#ffffff";
            ctx.fill();

            ctx.restore();

            if (mousePos.down && this.ui.selectedWaypoint === null) {
                const rotationPointRealCoords = new Translation2d();
                if (this.ui.rotatingWaypoint === null && !this.ui.rotating && new Translation2d(mousePos.x, mousePos.y).distance(
                    new Cartesian2d(ROBOT_SIZE.w.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) / 2, hardPoint.heading).asTranslation2d(
                        new Translation2d(
                            hardPoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                            hardPoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
                        ), false
                )) < 10) {
                    this.ui.rotating = true;
                    this.ui.rotatingWaypoint = hardPoint;
                } else if (this.ui.rotating && this.ui.rotatingWaypoint == hardPoint) {
                    const diffX = mousePos.x - this.ui.rotatingWaypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES);
                    const diffY = mousePos.y - this.ui.rotatingWaypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES);

                    const angle = -Math.atan2(diffY, diffX);

                    this.ui.rotatingWaypoint.heading = new Angle(angle, Angle.Type.RADIANS);

                    this.updateNumberEditor();
                }
            } else if (this.ui.rotating && this.ui.rotatingWaypoint === hardPoint && !mousePos.down) {
                this.ui.rotating = false;
                this.ui.rotatingWaypoint = null;
            }
        }
    }
    displayCurves(ctx) {
        for (let curve of this.curves) {
            ctx.beginPath();
            ctx.strokeStyle = "#e5e5e5";
            ctx.lineWidth = 2;
            ctx.moveTo(
                curve.points[0].x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                curve.points[0].y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            );

            for (let point of curve.calculatePath()) {
                ctx.lineTo(
                    point.getX().getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                    point.getY().getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                );
            }
            ctx.stroke();
        }
    }
    /**
     * Returns a list of BezierPath objects that correspond to the given Waypoint
     * @param {Number} hardWaypointIndex The index of the hard Waypoint from #getWaypoints() to get the corresponding BezierPath objects for
     * @returns {BezierPath[]} A list of BezierPath objects that correspond to the given Waypoint with length of two or 1 if first/last point
     */
    getCorrespondingCurves(hardWaypointIndex) {
        if (this.curves.length === 0) {
            return [];
        }

        if (hardWaypointIndex === 0) {
            return [this.curves[0]];
        } else if (hardWaypointIndex === this.getWaypoints(WaypointType.Hard).length - 1) {
            return [this.curves[this.curves.length - 1]];
        }

        return [this.curves[hardWaypointIndex - 1], this.curves[hardWaypointIndex]];
    }
    displaySoftWaypoints(ctx) {
        let i = 0;
        for (let curve of this.curves) {
            const soft1 = curve.controlPoint1;
            const soft2 = curve.controlPoint2;

            ctx.fillStyle = "black";
            ctx.strokeStyle = "black";
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.arc(
                soft1.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                soft1.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                4,
                0,
                2 * Math.PI
            );
            ctx.fill();

            ctx.beginPath();
            ctx.moveTo(
                soft1.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                soft1.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            );
            ctx.lineTo(
                curve.points[0].x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                curve.points[0].y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            );
            ctx.stroke();

            ctx.beginPath();
            ctx.arc(
                soft2.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                soft2.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                4,
                0,
                2 * Math.PI
            );
            ctx.fill();

            ctx.beginPath();
            ctx.moveTo(
                soft2.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                soft2.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            );
            ctx.lineTo(
                curve.points[1].x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                curve.points[1].y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            );
            ctx.stroke();
        }
    }
    getCorrespondingSoftWaypoints(ctx, hard_index) {
        const curves = this.getCorrespondingCurves(hard_index);
        if (curves.length === 1) {
            if (hard_index === this.getWaypoints(WaypointType.Hard).length - 1) {
                return [curves[0].controlPoint2];
            } else {
                return [curves[0].controlPoint1];
            }
        } else {
            return [curves[0].controlPoint2, curves[1].controlPoint1];
        }
    }
    displayWaypoints(ctx=new CanvasRenderingContext2D(), mouseInfo={}) {
        const r = 5;

        let i = 0;

        if (this.ui.selectedWaypoint !== null && !mouseInfo.down) {
            if (this.ui.lastSelectedWaypoint == null && this.ui.selectedWaypoint.type === WaypointType.Hard) {
                this.ui.lastSelectedWaypoint = this.ui.selectedWaypoint;
            }
            this.ui.selectedWaypoint = null;
            this.updateDOMMenu();
        }

        for (let waypoint of this.getWaypoints(WaypointType.Hard)) {
            ctx.fillStyle = this.ui.selectedWaypoint === waypoint ? "#d3d3d3" : "#ffffff";
            if (this.ui.lastSelectedWaypoint === waypoint && this.ui.selectedWaypoint !== waypoint) {
                ctx.fillStyle = "#9a6cbd";
            }
            ctx.beginPath();
            ctx.arc(
                waypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                waypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                r + (this.ui.selectedWaypoint === waypoint ? 2 : 0),
                0,
                2 * Math.PI
            );
            ctx.fill();

            if (mouseInfo.down && !this.ui.rotating && this.ui.selectedWaypoint === null && waypoint.AsTranslation2dUnit().toTranslation2d(INCHES_PER_PIXEL).distance(new Translation2d(mouseInfo.x, mouseInfo.y)) < r * 2) {
                this.ui.selectedWaypoint = waypoint;
                this.ui.lastSelectedWaypoint = null;
                this.updateDOMMenu();
            }

            if (this.ui.selectedWaypoint === waypoint && mouseInfo.down) {
                waypoint.x = new Unit(mouseInfo.x / INCHES_PER_PIXEL, Unit.Type.INCHES);
                waypoint.y = new Unit(mouseInfo.y / INCHES_PER_PIXEL, Unit.Type.INCHES);

                if (this.curves.length > 0) {
                    //get the corresponding BezierPath objects
                    const curves = this.getCorrespondingCurves(i);

                    curves.forEach((curve) => {
                        curve.calculatePath();
                    });
                }

                this.updateNumberEditor();
            } else if (this.ui.selectedWaypoint === waypoint && !mouseInfo.down) {
                this.ui.selectedWaypoint = null;
                this.ui.lastSelectedWaypoint = waypoint;
                this.updateDOMMenu();
            }

            //((this.ui.selectedWaypoint === waypoint && mouseInfo.down) || this.ui.lastSelectedWaypoint === waypoint)
            if (this.curves.length > 0) {
                const correspondingSoftWaypoints = this.getCorrespondingSoftWaypoints(ctx, i);

                ctx.fillStyle = "white";
                ctx.strokeStyle = "#aba9ab";
                for (let softWaypoint of correspondingSoftWaypoints) {
                    ctx.beginPath();
                    ctx.arc(
                        softWaypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                        softWaypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                        (r - 1) + (this.ui.selectedWaypoint === softWaypoint ? 1 : 0),
                        0,
                        2 * Math.PI
                    );

                    ctx.fill();
                    ctx.stroke();

                    ctx.beginPath();
                    ctx.moveTo(
                        waypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                        waypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
                    );
                    ctx.lineTo(
                        softWaypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                        softWaypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
                    );
                    ctx.stroke();

                    const distToMouse = softWaypoint.AsTranslation2dUnit().toTranslation2d(INCHES_PER_PIXEL).distance(new Translation2d(mouseInfo.x, mouseInfo.y));

                    if (this.ui.selectedWaypoint === null && !this.ui.rotating && mouseInfo.down && distToMouse < r * 2) {
                        this.ui.selectedWaypoint = softWaypoint;
                        this.ui.lastSelectedWaypoint = waypoint;
                        this.updateDOMMenu();
                    }

                    if (this.ui.selectedWaypoint === softWaypoint && mouseInfo.down) {
                        softWaypoint.x = new Unit(mouseInfo.x / INCHES_PER_PIXEL, Unit.Type.INCHES);
                        softWaypoint.y = new Unit(mouseInfo.y / INCHES_PER_PIXEL, Unit.Type.INCHES);

                        this.updateNumberEditor();
                    } else if (this.ui.selectedWaypoint === softWaypoint && !mouseInfo.down) {
                        this.ui.selectedWaypoint = null;
                        this.updateDOMMenu();
                    }
                }
            }

            i++;
        }
    }
    getTypedWaypointIndex(waypoint, type=WaypointType.Hard) {
        for (let i = 0; i < this.getWaypoints(type).length; i++) {
            if (waypoint.equals(this.getWaypoints(type)[i])) {
                return i;
            }
        }
    }
    addHardWaypoint(waypoint) {
        if (this.waypoints.length > 0) {
            this.waypoints.push(
                new Waypoint(
                    Unit.add(this.waypoints[this.waypoints.length - 1].x, new Unit(3, Unit.Type.INCHES)),
                    this.waypoints[this.waypoints.length - 1].y,
                    WaypointType.Soft
                )
            )
            this.waypoints.push(
                new Waypoint(
                    Unit.add(waypoint.x, new Unit(3, Unit.Type.INCHES)),
                    Unit.add(waypoint.y),
                    WaypointType.Soft
                )
            );
        }

        this.waypoints.push(waypoint);

        if (this.waypoints.length > 1) {
            this.curves.push(
                new BezierPath(
                    this.waypoints[this.waypoints.length - 4],
                    this.waypoints[this.waypoints.length - 3],
                    this.waypoints[this.waypoints.length - 2],
                    this.waypoints[this.waypoints.length - 1]
                )
            )

            this.curves[this.curves.length - 1].calculatePath();
        }

        this.updateList();
    }
    getWaypoints(type=WaypointType.Hard) {
        const waypoints = [];

        for (const waypoint of this.waypoints) {
            if (waypoint.type === type) {
                waypoints.push(waypoint);
            }
        }

        return waypoints;
    }
    updateList() {
        const waypointList = document.querySelector(".waypoint-list");

        waypointList.innerHTML = "";

        let n = 0;

        for (const waypoint of this.getWaypoints(WaypointType.Hard)) {
            const waypointListItem = document.createElement("div");

            waypointListItem.innerText = waypoint.name === "" ? `Waypoint ${n}` : waypoint.name;

            waypointListItem.addEventListener("click", () => {
                this.ui.selectedWaypoint = null;
                this.ui.lastSelectedWaypoint = waypoint;
                this.updateDOMMenu();
            });

            waypointList.appendChild(waypointListItem);

            n++;
        }

        const addWaypointButton = document.createElement("div");

        addWaypointButton.id = "add-waypoint";
        addWaypointButton.innerHTML = "+ <i>Add New Waypoint</i>";

        waypointList.appendChild(addWaypointButton);

        addWaypointButton.addEventListener("click", () => {
            this.addHardWaypoint(new Waypoint(
                CURRENT_FIELD_STANDARDS.half_width(),
                CURRENT_FIELD_STANDARDS.half_height(),
                WaypointType.Hard
            ));
        });

        this.updateDOMMenu();
    }
    updateDOMMenu() {
        const hardList = this.getWaypoints(WaypointType.Hard);
        for (let i = 0; i < hardList.length; i++) {
            const waypoint = hardList[i];

            const waypointListItem = document.querySelector(".waypoint-list").children[i];

            if ((this.ui.lastSelectedWaypoint != null && waypoint === this.ui.lastSelectedWaypoint) || (this.ui.selectedWaypoint != null && waypoint === this.ui.selectedWaypoint)) {
                waypointListItem.classList.add("active-waypoint-list-item");
            } else if (waypointListItem.classList.contains("active-waypoint-list-item")) {
                waypointListItem.classList.remove("active-waypoint-list-item");
            }
        }

        this.updateNumberEditor();
    }
    updateNumberEditor() {
        if (this.ui.lastSelectedWaypoint === null && this.ui.selectedWaypoint === null) {
            const waypointEditors = document.getElementsByClassName("single-waypoint-editor");
            for (let e of waypointEditors) {
                e.style.display = "none";
            }
            return;
        } else {
            let waypointIndex;
            if (this.ui.selectedWaypoint != null && this.ui.selectedWaypoint.type === WaypointType.Hard) {
                waypointIndex = this.getTypedWaypointIndex(this.ui.selectedWaypoint, WaypointType.Hard);
            } else if (this.ui.lastSelectedWaypoint != null) {
                waypointIndex = this.getTypedWaypointIndex(this.ui.lastSelectedWaypoint, WaypointType.Hard);
            } else {
                return;
            }

            const waypointEditors = document.getElementsByClassName("single-waypoint-editor");
            const curves = this.getCorrespondingCurves(waypointIndex);
            const waypoint = this.getWaypoints(WaypointType.Hard)[waypointIndex];

            waypointEditors[0].querySelector("#heading").value = round(waypoint.heading.get(Angle.Type.DEGREES));

            for (let i = 0; i < waypointEditors.length; i++) {
                if (i > 0 && curves.length > 0) {
                    waypointEditors[i].style.display = "flex";
                } else if (i === 0) {
                    waypointEditors[i].style.display = "flex";
                }

                const selector = domGetBasedOnOption(waypointEditors[i].querySelector(".select-unit"));

                if (i === 0) {
                    waypointEditors[i].querySelector("#x-coord").value = round(waypoint.x.get(selector));
                    waypointEditors[i].querySelector("#y-coord").value = round(waypoint.y.get(selector));
                } else if (i === 1 && curves.length > 0) {
                    waypointEditors[i].querySelector("#x-coord-soft-1").value = round(
                        (waypointIndex === 0 ? curves[0].controlPoint1 : curves[0].controlPoint2).x.get(selector)
                    );
                    waypointEditors[i].querySelector("#y-coord-soft-1").value = round(
                        (waypointIndex === 0 ? curves[0].controlPoint1 : curves[0].controlPoint2).y.get(selector)
                    );
                }

                if (i === 2 && (waypointIndex === 0 || waypointIndex === this.getWaypoints(WaypointType.Hard).length - 1)) {
                    waypointEditors[i].style.display = "none";
                } else if (i === 2 && curves.length > 0) {
                    waypointEditors[i].querySelector("#x-coord-soft-2").value = round(curves[1].controlPoint1.x.get(selector));
                    waypointEditors[i].querySelector("#y-coord-soft-2").value = round(curves[1].controlPoint1.y.get(selector));
                }
            }
        }
    }
    /**
     * Edit the path based on the DOM editor of the waypoint
     * @param {Number} index The index of the DOM editor that was edited (0 is hard point, 1 + 2 is soft points)
     * @param {String} variable The variable to edit
     * @param {String} raw_value The raw value of the variable
     * */
    editBasedOnDOMEditor(index, variable="x", raw_value="0", corresponding_unit="inches") {
        const value = parseFloat(raw_value);
        if (isNaN(value) || value === null) {
            return;
        }

        const unit = domGetBasedOnOption(document.querySelector(".select-unit"));

        /** based on the assumption that the canvas won't be edited at the same time dom is */
        const waypoint = this.ui.lastSelectedWaypoint;
        const waypointIndex = this.getTypedWaypointIndex(waypoint, WaypointType.Hard);
        const curves = this.getCorrespondingCurves(waypointIndex);

        if (index === 0) {
            if (variable === "x" || variable === "y") {
                waypoint[variable] = new Unit(value, unit);
            } else if (variable === "heading") {
                waypoint[variable] = new Angle(value, unit);
            } else {
                waypoint[variable] = value;
            }
        } else if (index === 1) {
            if (waypointIndex === 0) {
                curves[0].controlPoint1[variable] = new Unit(value, unit);
            } else {
                curves[0].controlPoint2[variable] = new Unit(value, unit);
            }
        } else if (index === 2) {
            curves[1].controlPoint1[variable] = new Unit(value, unit);
        }
    }
}

function addDOMEditorEventListeners() {
    document.querySelector("#y-coord").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(0, "y", e.target.value, e.target.parentElement.querySelector(".select-unit").value);
    });
    document.querySelector("#x-coord").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(0, "x", e.target.value, e.target.parentElement.querySelector(".select-unit").value);
    });
    document.querySelector("#x-coord-soft-1").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(1, "x", e.target.value, e.target.parentElement.querySelector(".select-unit").value);
    });
    document.querySelector("#y-coord-soft-1").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(1, "y", e.target.value, e.target.parentElement.querySelector(".select-unit").value);
    });
    document.querySelector("#x-coord-soft-2").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(2, "x", e.target.value, e.target.parentElement.querySelector(".select-unit").value);
    });
    document.querySelector("#y-coord-soft-2").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(2, "y", e.target.value, e.target.parentElement.querySelector(".select-unit").value);
    });

    document.querySelector("#heading").addEventListener("input", (e) => {
        currentPath.editBasedOnDOMEditor(0, "heading", e.target.value, e.target.parentElement.querySelector(".select-unit-angle").value);
    });
}

async function sendCurrentPathToAPI() {
    let body = {};

    let i = 0;
    for (let waypoint of currentPath.waypoints) {
        const info = {
            x: waypoint.x.get(Unit.Type.CENTIMETERS),
            y: waypoint.y.get(Unit.Type.CENTIMETERS),
            heading: waypoint.heading.get(Angle.Type.RADIANS),
            type: waypoint.type === WaypointType.Hard ? 0 : 1
        }

        body["waypoint_" + i] = `${info.x},${info.y},${info.heading},${info.type}`;

        i++;
    }

    body["waypoint_count"] = currentPath.waypoints.length;

    const req = await fetch("/api/paths/send", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });

    const res = await req.json();

    if (res.success) {
        alert("Successfully sent path to robot!");
    } else {
        alert("Failed to send path to robot!");
        console.error(res.error);
    }
}


function domGetBasedOnOption(option) {
    const units = {
        "inches": Unit.Type.INCHES,
        "feet": Unit.Type.FEET,
        "meters": Unit.Type.METERS,
        "centimeters": Unit.Type.CENTIMETERS,
        "radians": Angle.Radians,
        "degrees": Angle.Degrees
    }

    return units[option.value];
}

setTimeout(() => {
    addDOMEditorEventListeners();
}, 500)
