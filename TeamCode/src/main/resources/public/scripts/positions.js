
class Translation2d {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    distance(other) {
        if (!(other instanceof Translation2d)) {
            throw new Error("other must be a Translation2d!");
        }

        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    static Zero() {
        return new Translation2d(0, 0);
    }
}

class Translation2dUnit extends Translation2d {
    constructor(x=Unit.Zero(), y=Unit.Zero()) {
        if (!(x instanceof Unit) || !(y instanceof Unit)) {
            throw new Error("x and y must be a Unit!");
        }

        super(x.get(Unit.Type.CENTIMETERS), y.get(Unit.Type.CENTIMETERS));
    }

    toTranslation2d(inchesToPixels=1) {
        return new Translation2d(new Unit(this.x, Unit.Type.CENTIMETERS).getcu(inchesToPixels, Unit.Type.INCHES), new Unit(this.y, Unit.Type.CENTIMETERS).getcu(inchesToPixels, Unit.Type.INCHES));
    }

    getX() {
        return new Unit(this.x, Unit.Type.CENTIMETERS);
    }

    getY() {
        return new Unit(this.y, Unit.Type.CENTIMETERS);
    }
}

class Cartesian2d {
    constructor(r=0, theta=new Angle(0, Angle.Type.DEGREES)) {
        this.r = r;
        this.theta = theta;
    }
    asTranslation2d(translation=new Translation2d(0,0), useClockwisePositive=true) {
        return new Translation2d(
            translation.x + this.r * Math.cos((useClockwisePositive ? 1 : -1) * this.theta.get(Angle.Type.RADIANS)),
            translation.y + this.r * Math.sin((useClockwisePositive ? 1 : -1) * this.theta.get(Angle.Type.RADIANS))
        )
    }
}