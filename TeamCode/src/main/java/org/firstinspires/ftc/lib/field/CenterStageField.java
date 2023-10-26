package org.firstinspires.ftc.lib.field;

import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;

/**
 * This class represents the field for the 2023-2024 season, Center Stage.
 * This class is a singleton, and should be accessed through the static instance variable.
 */
public class CenterStageField extends Field {

    private static final Unit tile = new Unit(23d + ((double) 1 /8), Unit.Type.Inches);
    private static final Unit fieldWidth = new Unit(tile.get(Unit.Type.Inches) * 6, Unit.Type.Inches);
    private static final Unit fieldHeight = new Unit(tile.get(Unit.Type.Inches) * 6, Unit.Type.Inches);


    public CenterStageField() {
        double[] distances = {
                21 + 1d/2,
                22 + 1d/2,
                46 + 1d/4,
                22 + 1d/2,
        };

        Unit truss_y = new Unit(tile.get(Unit.Type.Inches) * 3.5, Unit.Type.Inches);

        double tot = 0;

        //setup truss obsticales.
        for (int i = 0; i < distances.length; i++) {
            tot += distances[i];

            obstacles[i + 2] = new Obstacle(
                    new Unit(tot, Unit.Type.Inches),
                    new Unit(truss_y.get(Unit.Type.Inches) - tile.get(Unit.Type.Inches), Unit.Type.Inches),
                    new Unit(3, Unit.Type.Inches),
                    new Unit(3, Unit.Type.Inches)
            );

            obstacles[i + 2 + 4] = new Obstacle(
                    new Unit(tot, Unit.Type.Inches),
                    new Unit(truss_y.get(Unit.Type.Inches) + tile.get(Unit.Type.Inches) - 3, Unit.Type.Inches),
                    new Unit(3, Unit.Type.Inches),
                    new Unit(3, Unit.Type.Inches)
            );
        }
    }

    /**
     * all obstacles are in relative to the top left of the field.
     *
     */
    private Obstacle[] obstacles = {
        new Obstacle( //Backdrop BLUE
            new Unit(22d + (1d/2), Unit.Type.Inches),
            new Unit(0d, Unit.Type.Inches),
            new Unit(25d + (5d/8), Unit.Type.Inches),
            new Unit(11 + (1d/4), Unit.Type.Inches)
        ),
        new Obstacle( //Backdrop RED
            new Unit(fieldWidth.get(Unit.Type.Inches) - (22d + (1d/2)) - (25d + (5d/8)), Unit.Type.Inches),
            new Unit(0, Unit.Type.Inches),
            new Unit(25d + (5d / 8), Unit.Type.Inches),
            new Unit(11d + (1d / 4), Unit.Type.Inches)
        ),
        //truss supports TODO
        new Obstacle(), //1st top
        new Obstacle(), //1st bottom
        new Obstacle(), //2nd top
        new Obstacle(), //2nd bottom
        new Obstacle(), //3rd top
        new Obstacle(), //3rd bottom
        new Obstacle(), //4th top
        new Obstacle(), //4th bottom
    };

    public Obstacle[] getObstacles() {
        return obstacles;
    }

}
