package org.firstinspires.ftc.lib.pathing;

import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;

import java.util.*;

import static java.lang.Math.pow;

public class FourPointBezier {
    private Waypoint m_start;
    private Waypoint m_end;
    private Waypoint m_control1;
    private Waypoint m_control2;

    private ArrayList<Translation2d> m_points;

    /**
     * Creates a new FourPointBezier object
     * @param start the starting point
     * @param end the ending point
     * @param control1 the first control point
     * @param control2 the second control point
     */
    public FourPointBezier(Waypoint start, Waypoint control1, Waypoint control2, Waypoint end) {
        m_start = start;
        m_end = end;
        m_control1 = control1;
        m_control2 = control2;

        m_points = new ArrayList<>();
    }

    /**
     * Returns the waypoints of the Bézier curve
     * @return the waypoints of the Bézier curve
     */
    public Waypoint[] getWaypoints() {
        return new Waypoint[] {m_start, m_control1, m_control2, m_end};
    }

    /**
     * Returns the generated points of the Bézier curve. If the curve has not been generated, it will return the basic curve with a step size of 0.01.
     * @return the generated points of the Bézier curve.
     */
    public ArrayList<Translation2d> getPoints() {
        if (m_points.isEmpty()) {
            generate();
        }

        return m_points;
    }

    /**
     * Returns the x-coordinate of the Bézier curve at t
     * @param t the value of t
     * @return the x-coordinate of the Bézier curve at t
     */
    private double b_x(double t) {
        //p1(-t^3 + 3t^2 - 3t + 1)
        // + 3p2(t^3 - 2t^2 + t)
        // + 3p3(-t^3 + t^2)
        // + p4(t^3)
        return (m_start.getX() * (-pow(t, 3) + (3*pow(t, 2)) - (3*t) + 1))
                + (3 * m_control1.getX() * (pow(t, 3) - (2 * pow(t, 2)) + t))
                + (3 * m_control2.getX() * (-pow(t, 3) + pow(t, 2)))
                + (m_end.getX() * (pow(t, 3)));
    }

    /**
     * Returns the y-coordinate of the Bézier curve at t
     * @param t the value of t
     * @return the y-coordinate of the Bézier curve at t
     */
    private double b_y(double t) {
        //p1(-t^3 + 3t^2 - 3t + 1) + 3p2(t^3 - 2t^2 + t) + 3p3(-t^3 + t^2) + p4(t^3)
        return (m_start.getY() * (-pow(t, 3) + (3*pow(t, 2)) - (3*t) + 1))
                + (3 * m_control1.getY() * (pow(t, 3) - (2*pow(t, 2)) + t))
                + (3 * m_control2.getY() * (-pow(t, 3) + pow(t, 2)))
                + (m_end.getY() * (pow(t, 3)));
    }

    /**
     * Returns the first derivative of the Bézier curve's x-points at t
     * @param t the value of t
     * @return the first derivative of the Bézier curve's x-points at t
     */
    private double b_dx(double t) {
        //p(-3t^2 + 6t - 3) + 3p(3t^2 - 4t + 1) + 3p(-3t^2 + 2t) + p(3t^2)
        return m_start.getX()*(-3*pow(t, 2) + 6*t - 3) + 3* m_control1.getX()*(3*pow(t, 2) - 4*t + 1) + 3* m_control2.getX()*(-3*pow(t, 2) + 2*t) + m_end.getX()*(3*pow(t, 2));
    }

    /**
     * Returns the first derivative of the Bézier curve's y-points at t
     * @param t the value of t
     * @return the first derivative of the Bézier curve's y-points at t
     */
    private double b_dy(double t) {
        return m_start.getY()*(-3*pow(t, 2) + 6*t - 3) + 3* m_control1.getY()*(3*pow(t, 2) - 4*t + 1) + 3* m_control2.getY()*(-3*pow(t, 2) + 2*t) + m_end.getY()*(3*pow(t, 2));
    }

    /**
     * Returns the second derivative of the Bézier curve's x-points at t
     * @param t the value of t
     * @return the second derivative of the Bézier curve's x-points at t
     */
    private double b_dx2(double t) {
        //p(-6t + 6) + 3p(6t - 4) + 3p(-6t + 2) + p(6t)
        return m_start.getX()*(-6*t + 6) + 3*m_control1.getX()*(6*t - 4) + 3* m_control2.getX()*(-6*t + 2) + m_end.getX()*(6*t);
    }

    /**
     * Returns the second derivative of the Bézier curve's y-points at t
     * @param t the value of t
     * @return the second derivative of the Bézier curve's y-points at t
     */
    private double b_d2y(double t) {
        return m_start.getY()*(-6*t + 6) + 3*m_control1.getY()*(6*t - 4) + 3* m_control2.getY()*(-6*t + 2) + m_end.getY()*(6*t);
    }

    /**
     * Returns the first derivative of the Bézier curve at t
     * @param t the value of t
     * @return the first derivative of the Bézier curve at t
     */
    private double b_d1(double t) {
        return b_dy(t) / b_dx(t);
    }

    /**
     * Returns the velocity of the Bézier curve at t (literally the same thing as the first derivative
     * @param t the value of t
     * @return the velocity of the Bézier curve at t
     */
    private double velocity(double t) {
        return b_d1(t);
    }

    /***
     * Returns the second derivative of the Bézier curve at t
     * @param t the value of t
     * @return the second derivative of the Bézier curve at t
     */
    private double b_d2(double t) {
        return b_d2y(t) / b_dx2(t);
    }

    /**
     * Returns the curvature of the Bézier curve at t
     * @param t the value of t
     * @return the curvature of the Bézier curve at t
     */
    private double kappa(double t) {
        return Math.abs(b_dx(t) * b_d2y(t) - b_dy(t) * b_dx2(t)) / pow(pow(b_dx(t), 2) + pow(b_dy(t), 2), 1.5);
    }

    /**
     * Returns the direction of the curvature of the Bézier curve at t
     * @param t the value of t
     * @return the direction of the curvature of the Bézier curve at t
     */
    private double curvatureDirection(double t) {
        return Math.signum(b_d1(t)) * Math.signum(b_d2(t));
    }

    /**
     * Returns the centripetal force of the Bézier curve at t
     * @param t the value of t
     * @param mass The mass of the robot
     * @param velocity_at_t The velocity of the robot at t
     * @param basedOnKiloUnits True to assume path is in meters, so use the units of kg and m/s^2. False to assume path is in centimeters, so use the units of g and cm/s^2.
     * @return The centripetal force of the Bézier curve at t
     */
    private double centripetalForce(double t, Unit mass, Unit velocity_at_t, boolean basedOnKiloUnits) {
        double r = 1/kappa(t);

        return mass.get(basedOnKiloUnits ? Unit.Type.Meters : Unit.Type.Centimeters) * (pow(velocity_at_t.get(basedOnKiloUnits ? Unit.Type.Meters : Unit.Type.Centimeters), 2) / r);
    }

    /***
     * Generates the Bézier curve with a step size of 0.01, the basic way to generate the curve.
     */
    public void generate() {
        m_points.clear();

        for (double t = 0; t <= 1; t += 0.01) {
            m_points.add(new Translation2d(b_x(t), b_y(t)));
        }

        m_points.add(new Translation2d(b_x(1), b_y(1)));
    }

    /**
     * Generates a lookup table for the Bézier curve
     * @param t_step the value of t to increment by. The smaller the value, the more accurate the curve will be.
     * @return the lookup table, a HashMap with the distance as the key and the point as the value
     */
    public HashMap<Double, Translation2d> generateLookupTable(double t_step) {
        HashMap<Double, Translation2d> lookupTable = new HashMap<>();

        double distance = 0;
        Translation2d lastPoint = new Translation2d(b_x(0), b_y(0));
        for (double t = 0; t <= 1; t += t_step) {
            Translation2d point = new Translation2d(b_x(t), b_y(t));

            point.addAttribute("t", t);

            distance += point.distance(lastPoint);

            point.addAttribute("distance", distance);
            lookupTable.put(distance, point);

            lastPoint = point;
        }

        return lookupTable;
    }

    /**
     * Returns the length of the Bézier curve
     * @return the length of the Bézier curve
     */
    public double length() {
        HashMap<Double, Translation2d> lookupTable = generateLookupTable(0.01);

        return lookupTable.keySet().stream().max(Double::compareTo).get();
    }

    private final boolean verbose = true;

    /***
     * Generates the Bézier curve to be evenly spaced
     * @param stepSize the distance between each point
     * @param t_step the value of t to increment by. The smaller the value, the more accurate the curve will be.
     */
    public void generateEvenlySpaced(double stepSize, double t_step) {
        HashMap<Double, Translation2d> lookupTable = generateLookupTable(t_step);

        double maxDistance = length();

        m_points.clear();

        double lastDistance = 0;

        for (double d = 0; d <= maxDistance; d += stepSize) {
            Translation2d lastPoint = lookupTable.get(lookupTable.keySet().toArray(new Double[0])[0]);

            for (int i = 0; i < lookupTable.keySet().size(); i++) {
                double p_dist = lookupTable.keySet().toArray(new Double[0])[i];

                if (p_dist > d) {
                    Translation2d point = lookupTable.get(p_dist);
                    if (i > 0) {
                        double dx = point.getX() - lastPoint.getX();
                        double dy = point.getY() - lastPoint.getY();

                        double weight = (d - lastDistance) / (p_dist - lastDistance);

                        m_points.add(new Translation2d(lastPoint.getX() + (dx * weight), lastPoint.getY() + (dy * weight)));
                    } else {
                        m_points.add(point);
                    }

                    break;
                }

                lastPoint = lookupTable.get(p_dist);
            }

            lastDistance = d;
        }
    }

    /**
     * Generates the Bézier curve to be spaced by the PID controller.
     * @param t_step the value of t to increment by. The smaller the value, the more accurate the curve will be but longer it will take to generate.
     * @param controller the PID controller to be used
     * @param min_distance the minimum distance between each point
     * @param max_distance the maximum distance between each point
     */
    public void generateByPID(double t_step, PIDController controller, double min_distance, double max_distance) {
        generateByPID(t_step, controller, min_distance, max_distance, 0);
    }

    /**
     * Generate the Bézier curve to be spaced by the PID controller.
     * @param t_step the value of t to increment by. The smaller the value, the more accurate the curve will be but the longer it will take to generate.
     * @param controller the PID controller to be used
     * @param min_distance the minimum distance between each point
     * @param max_distance the maximum distance between each point
     * @param total_distance the total distance of the curve. If 0, it will use the length of this Bézier curve alone.
     * */
    public void generateByPID(double t_step, PIDController controller, double min_distance, double max_distance, double total_distance) {
        if (!controller.initialized()) {
            controller.setSetpoint(0);
            if (verbose) {
                System.out.println("PID controller not initialized, setting setpoint to 0.");
            }
        }

        //Adding support so the curve can be generated with multiple curves.
        double beginningDistance = controller.getSetpoint();

        HashMap<Double, Translation2d> lookupTable = generateLookupTable(t_step);

        double maxDistance = total_distance < 1 ? length() : total_distance;

        m_points.clear();

        if (verbose) {
            System.out.println("generated lookup table, length: " + maxDistance + " cm");
        }

        Double[] rawDistances = lookupTable.keySet().toArray(new Double[0]);

        List<Double> sortedDistances = Arrays.asList(rawDistances);

        Collections.sort(sortedDistances);

        for (int i = 0; i < sortedDistances.size(); i++) {
            System.out.println("Lookup table dist: " + sortedDistances.get(i));
        }

        double lastDistance = 0;
        Translation2d lastPoint = new Translation2d(b_x(0), b_y(0));

        double d = beginningDistance;

        while (d < maxDistance) {
            double newCalc = controller.calculate(d, maxDistance);

            if (newCalc / AutonomousConstants.getDeltaTime() > max_distance) {
                newCalc = max_distance;
                System.out.println("PID too high, setting to max velocity");
            } else if (newCalc / AutonomousConstants.getDeltaTime() < min_distance) {
                newCalc = min_distance;
                System.out.println("PID too low, setting to min velocity");
            }

            d += newCalc;

            System.out.println("Moving " + newCalc + "cm, currently at: " + d);

            for (int i = 0; i < sortedDistances.size(); i++) {
                double p_dist = sortedDistances.get(i);

                if (p_dist > d) {
                    Translation2d point = lookupTable.get(p_dist);
                    if (i > 0) {
                        double dx = point.getX() - lastPoint.getX();
                        double dy = point.getY() - lastPoint.getY();

                        double weight = (d - lastDistance) / (p_dist - lastDistance);

                        Translation2d newPoint = new Translation2d(lastPoint.getX() + dx * weight, lastPoint.getY() + dy * weight);

                        newPoint.addAttribute("t", point.getAttribute("t"));
                        newPoint.addAttribute("last-t", lastPoint.getAttribute("t"));

                        m_points.add(newPoint);
                        lastPoint = newPoint;
                    } else {
                        m_points.add(point);
                        lastPoint = point;
                    }

                    break;
                }
            }

            lastDistance = d;
        }
    }

}
