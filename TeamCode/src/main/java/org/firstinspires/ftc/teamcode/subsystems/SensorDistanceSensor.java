package org.firstinspires.ftc.teamcode.subsystems;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Sensor: Distance Sensor", group = "Sensor")
//@Disabled                            // Comment this out to add to the opmode list
public class SensorDistanceSensor extends LinearOpMode {
    DistanceSensor sensorDistance;

    @Override
    public void runOpMode() {

        // get a reference to the distance sensor that shares the same name.
        sensorDistance = hardwareMap.get(DistanceSensor.class, "distance");

        // wait for the start button to be pressed.
        waitForStart();

        // loop and read the distance data.
        while (opModeIsActive()) {
            double distance = sensorDistance.getDistance(DistanceUnit.CM);

            // check if the distance is less than or equal to 10cm
            if (distance <= 10) {
                telemetry.addData("Status", "Stop");
            } else {
                telemetry.addData("Status", "Moving");
            }

            telemetry.addData("Distance (cm)", distance);
            telemetry.update();
        }
    }
}
