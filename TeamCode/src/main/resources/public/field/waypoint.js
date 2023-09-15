
const WaypointType = {
    Hard: 0, // Hard waypoints are points that the robot must pass through
    Soft: 1 // Soft waypoints are used as control points for the path
}

class Waypoint {
    static equals(waypoint1, waypoint2) {
        return Unit.closeEquals(waypoint1.x, waypoint2.x, 0.01) &&
            Unit.closeEquals(waypoint1.y, waypoint2.y, 0.01) &&
            waypoint1.type === waypoint2.type;
    }
    constructor(x=Unit.Zero(), y=Unit.Zero(), type=WaypointType.Hard) {
        if (!(x instanceof Unit) || !(y instanceof Unit)) {
            throw new Error("x and y must be a Unit!");
        }

        this.x = x;
        this.y = y;

        this.type = type;

        this.name = "";

        this.heading = Angle.Zero(); 
    }
    AsTranslation2dUnit() {
        return new Translation2dUnit(this.x, this.y);
    }
    add(x=Unit.Zero(), y=Unit.Zero()) {
        if (!(x instanceof Unit) || !(y instanceof Unit)) {
            throw new Error("x and y must be a Unit!");
        }

        const newWaypoint = new Waypoint(Unit.add(this.x, x), Unit.add(this.y, y), this.type);

        newWaypoint.heading = this.heading;

        return newWaypoint;
    }

    equals(other) {
        return Waypoint.equals(this, other);
    }
}

class BezierPath {
    constructor(point1, control1, control2, point2) {
        if (!(point1 instanceof Waypoint) || !(point2 instanceof Waypoint)) {
            throw new Error("point1 and point2 must be Waypoints!");
        }

        this.controlPoint1 = control1;
        this.controlPoint1.type = WaypointType.Soft;

        this.controlPoint2 = control2;
        this.controlPoint2.type = WaypointType.Soft;

        this.points = [point1, point2];

        this.cache = {
            path: null,
            p1: null,
            p2: null,
            c1: null,
            c2: null
        }
    }
    factorial(n) {
        let result = 1;
        for (let i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    binomial(n, i) {
        return this.factorial(n) / (this.factorial(i) * this.factorial(n - i));
    }
    bernstein(n, i, t) {
        return this.binomial(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i);
    }
    updateCache() {
        this.cache.path = null;

        //make copies of the points
        this.cache.p1 = new Waypoint(
            new Unit(this.points[0].x.get(Unit.Type.INCHES), Unit.Type.INCHES),
            new Unit(this.points[0].y.get(Unit.Type.INCHES), Unit.Type.INCHES)
        );
        this.cache.p2 = new Waypoint(
            new Unit(this.points[1].x.get(Unit.Type.INCHES), Unit.Type.INCHES),
            new Unit(this.points[1].y.get(Unit.Type.INCHES), Unit.Type.INCHES)
        );
        this.cache.c1 = new Waypoint(
            new Unit(this.controlPoint1.x.get(Unit.Type.INCHES), Unit.Type.INCHES),
            new Unit(this.controlPoint1.y.get(Unit.Type.INCHES), Unit.Type.INCHES),
            WaypointType.Soft
        );
        this.cache.c2 = new Waypoint(
            new Unit(this.controlPoint2.x.get(Unit.Type.INCHES), Unit.Type.INCHES),
            new Unit(this.controlPoint2.y.get(Unit.Type.INCHES), Unit.Type.INCHES),
            WaypointType.Soft
        );
    }
    cacheUpdated() {
        if (this.cache.p1 === null) return false;
        if (this.cache.p2 === null) return false;
        if (this.cache.c1 === null) return false;
        if (this.cache.c2 === null) return false;
        return Waypoint.equals(this.cache.p1, this.points[0]) &&
            Waypoint.equals(this.cache.p2, this.points[1]) &&
            Waypoint.equals(this.cache.c1, this.controlPoint1) &&
            Waypoint.equals(this.cache.c2, this.controlPoint2);
    }
    calculatePath() {
        if (this.cacheUpdated()) {
            return this.cache.path;
        }

        const points = [this.points[0], this.controlPoint1, this.controlPoint2, this.points[1]];

        const path = [];

        const n = this.points.length;
        for (let t = 0; t < 1; t += 0.01) {
            let x = 0;
            let y = 0;

            x += points[0].x.get(Unit.Type.INCHES) * (-Math.pow(t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1);
            x += points[1].x.get(Unit.Type.INCHES) * (3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 3 * t);
            x += points[2].x.get(Unit.Type.INCHES) * (-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2));
            x += points[3].x.get(Unit.Type.INCHES) * Math.pow(t, 3);

            y += points[0].y.get(Unit.Type.INCHES) * (-Math.pow(t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1);
            y += points[1].y.get(Unit.Type.INCHES) * (3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 3 * t);
            y += points[2].y.get(Unit.Type.INCHES) * (-3 * Math.pow(t, 3) + 3 * Math.pow(t, 2));
            y += points[3].y.get(Unit.Type.INCHES) * Math.pow(t, 3);

            path.push(
                new Translation2dUnit(
                    new Unit(x, Unit.Type.INCHES),
                    new Unit(y, Unit.Type.INCHES)
                )
            );
        }

        this.updateCache();
        this.cache.path = path;

        return path;
    }
}

class Path {
    constructor() {
        this.waypoints = [];
        this.curves = []; //list of BezierPath objects

        this.ui = {
            selectedWaypoint: null,
            lastSelectedWaypoint: null,
        }
    }
    displayCurves(ctx) {
        for (let curve of this.curves) {
            for (let point of curve.calculatePath()) {
                ctx.beginPath();
                ctx.arc(
                    point.getX().getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                    point.getY().getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                    1,
                    0,
                    2 * Math.PI
                );
                ctx.stroke();
            }
        }
    }
    /**
     * Returns a list of BezierPath objects that correspond to the given Waypoint
     * @param {Number} hardWaypointIndex The index of the hard Waypoint from #getWaypoints() to get the corresponding BezierPath objects for
     * @returns {BezierPath[]} A list of BezierPath objects that correspond to the given Waypoint with length of two or 1 if first/last point
     */
    getCorrespondingCurves(hardWaypointIndex) {
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
        const r = 4;

        let i = 0;

        if (this.ui.selectedWaypoint !== null && !mouseInfo.down) {
            if (this.ui.lastSelectedWaypoint == null && this.ui.selectedWaypoint.type === WaypointType.Hard) {
                this.ui.lastSelectedWaypoint = this.ui.selectedWaypoint;
            }
            this.ui.selectedWaypoint = null;
            this.updateDOMMenu();
        }

        for (let waypoint of this.getWaypoints(WaypointType.Hard)) {
            ctx.fillStyle = this.ui.selectedWaypoint === waypoint ? "red" : "black";
            ctx.beginPath();
            ctx.arc(
                waypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                waypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                r,
                0,
                2 * Math.PI
            );
            ctx.fill();

            if (mouseInfo.down && this.ui.selectedWaypoint === null && waypoint.AsTranslation2dUnit().toTranslation2d(INCHES_PER_PIXEL).distance(new Translation2d(mouseInfo.x, mouseInfo.y)) < r) {
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
            } else if (this.ui.selectedWaypoint === waypoint && !mouseInfo.down) {
                this.ui.selectedWaypoint = null;
                this.ui.lastSelectedWaypoint = waypoint;
                this.updateDOMMenu();
            }

            if (((this.ui.selectedWaypoint === waypoint && mouseInfo.down) || this.ui.lastSelectedWaypoint === waypoint) && this.curves.length > 0) {
                const correspondingSoftWaypoints = this.getCorrespondingSoftWaypoints(ctx, i);

                ctx.fillStyle = "purple";
                ctx.strokeStyle = "purple";
                for (let softWaypoint of correspondingSoftWaypoints) {
                    ctx.beginPath();
                    ctx.arc(
                        softWaypoint.x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                        softWaypoint.y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                        r,
                        0,
                        2 * Math.PI
                    );

                    ctx.fill();

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

                    if (this.ui.lastSelectedWaypoint === waypoint && mouseInfo.down && softWaypoint.AsTranslation2dUnit().toTranslation2d(INCHES_PER_PIXEL).distance(new Translation2d(mouseInfo.x, mouseInfo.y)) < r) {
                        this.ui.selectedWaypoint = softWaypoint;
                        this.updateDOMMenu();
                    }

                    if (this.ui.selectedWaypoint === softWaypoint && mouseInfo.down) {
                        softWaypoint.x = new Unit(mouseInfo.x / INCHES_PER_PIXEL, Unit.Type.INCHES);
                        softWaypoint.y = new Unit(mouseInfo.y / INCHES_PER_PIXEL, Unit.Type.INCHES);
                    } else if (this.ui.selectedWaypoint === waypoint && !mouseInfo.down) {
                        this.ui.selectedWaypoint = null;
                        this.updateDOMMenu();
                    }
                }
            }

            i++;
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

            if ((this.ui.lastSelectedWaypoint != null && waypoint.equals(this.ui.lastSelectedWaypoint)) || (this.ui.selectedWaypoint != null && waypoint.equals(this.ui.selectedWaypoint))) {
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
            const waypointEditors = document.getElementsByClassName("single-waypoint-editor");
            for (let e of waypointEditors) {
                e.style.display = "flex";
            }
        }
    }
}
