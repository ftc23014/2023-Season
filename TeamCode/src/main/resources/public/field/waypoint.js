

class Waypoint {
    constructor(x=Unit.Zero(), y=Unit.Zero()) {
        if (!(x instanceof Unit) || !(y instanceof Unit)) {
            throw new Error("x and y must be a Unit!");
        }

        this.x = x;
        this.y = y;

        this.heading = Angle.Zero(); 
    }
}