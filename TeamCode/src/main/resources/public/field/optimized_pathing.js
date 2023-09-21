

class OptimizedPath {
    constructor() {
        this.waypoints = [];
        this.path = [];

        this.ui = {
            selectedWaypoint: null,
            lastSelectedWaypoint: null,
            selected() {
                if (this.selectedWaypoint == null) {
                    if (this.lastSelectedWaypoint != null) {
                        return this.lastSelectedWaypoint;
                    } else {
                        return null;
                    }
                } else {
                    return this.selectedWaypoint;
                }
            }
        }
    }
    calculateMostOptimizedPath() {

    }
    addWaypoint(waypoint=new Waypoint()) {
        this.waypoints.push(waypoint);
    }
    updateList() {
        const waypointList = document.querySelector(".waypoint-list");

        waypointList.innerHTML = "";

        let n = 0;

        for (const waypoint of this.waypoints) {
            const waypointListItem = document.createElement("div");

            waypointListItem.innerText = waypoint.name === "" ? `Waypoint ${n}` : waypoint.name;

            const ncopy = n + 1 - 1;

            waypointListItem.addEventListener("click", () => {
                this.ui.selectedWaypoint = null;
                this.ui.lastSelectedWaypoint = ncopy;
            });

            waypointList.appendChild(waypointListItem);

            n++;
        }

        const addWaypointButton = document.createElement("div");

        addWaypointButton.id = "add-waypoint";
        addWaypointButton.innerHTML = "+ <i>Add New Waypoint</i>";

        waypointList.appendChild(addWaypointButton);

        addWaypointButton.addEventListener("click", () => {
            this.addWaypoint(new Waypoint(
                CURRENT_FIELD_STANDARDS.half_width(),
                CURRENT_FIELD_STANDARDS.half_height(),
                WaypointType.Hard
            ));
        });
    }
    displayWaypoints(ctx, mouseInfo) {
        for (let i = 0; i < this.waypoints.length; i++) {
            ctx.beginPath();
            //no stroke
            ctx.lineWidth = 0;
            if (this.ui.selected() === i) {
                if (this.ui.selectedWaypoint === i) {
                    ctx.fillStyle = "#661d7a";
                } else {
                    ctx.fillStyle = "#cf9dff";
                }
            } else {
                ctx.fillStyle = "white";
            }
            ctx.arc(
                this.waypoints[i].x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                this.waypoints[i].y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                5, 0, 2 * Math.PI
            );
            ctx.fill();

            if (this.ui.selectedWaypoint === i) {
                if (mouseInfo.down) {
                    this.waypoints[i].x = new Unit(mouseInfo.x / INCHES_PER_PIXEL, Unit.Type.INCHES);
                    this.waypoints[i].y = new Unit(mouseInfo.y / INCHES_PER_PIXEL, Unit.Type.INCHES);
                } else {
                    this.ui.selectedWaypoint = null;
                    this.ui.lastSelectedWaypoint = i;
                }
            } else if (mouseInfo.down) {
                if (this.waypoints[i].AsTranslation2dUnit().toTranslation2d(INCHES_PER_PIXEL).distance(new Translation2d(mouseInfo.x, mouseInfo.y)) < 5) {
                    this.ui.selectedWaypoint = i;
                    this.ui.lastSelectedWaypoint = null;
                }
            }
        }
    }
    displayCurves(ctx) {

    }
    editBasedOnDOMEditor(index, variable="x", raw_value="0", corresponding_unit="inches") {

    }
}