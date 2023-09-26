
const WaypointType = {
    Hard: 0, // Hard waypoints are points that the robot must pass through
    Soft: 1 // Soft waypoints are used as control points for the path
}

function round(n, places=3) {
    return Math.round(n * Math.pow(10, places)) / Math.pow(10, places);
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
    updateList() {

    }
    displayWaypoints(ctx, mouseInfo) {

    }
    displayCurves(ctx) {

    }
    editBasedOnDOMEditor(index, variable="x", raw_value="0", corresponding_unit="inches") {

    }
}
