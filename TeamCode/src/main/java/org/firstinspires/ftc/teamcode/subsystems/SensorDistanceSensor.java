package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Sensor: Distance Sensor", group = "Sensor")
//@Disabled                            // Comment this out to add to the opmode list
public class SensorDistanceSensor extends LinearOpMode {
    DistanceSensor sensorDistance;
    boolean objectWithin3cm = false;
    long objectDetectedTime = 0;
    int numOfPixels = 0;

    @Override
    public void runOpMode() {

        // get a reference to the distance sensor that shares the same name.
        sensorDistance = hardwareMap.get(DistanceSensor.class, "distance");

        // wait for the start button to be pressed.
        waitForStart();

        // loop and read the distance data.
        while (opModeIsActive()) {
            double distance = sensorDistance.getDistance(DistanceUnit.CM);

            if (distance <= 3 && !objectWithin3cm) {
                objectWithin3cm = true;
                objectDetectedTime = System.currentTimeMillis();
                telemetry.addData("Status", "Object within 3cm");
            } else if (distance > 3 && objectWithin3cm) {
                objectWithin3cm = false;
                long elapsedTime = System.currentTimeMillis() - objectDetectedTime;
                if (elapsedTime >= 500) {  // 0.5 seconds
                    numOfPixels++;
                    telemetry.addData("Status", "Pixel count increased");
                }
            }

            if (numOfPixels >= 2) {
                telemetry.addData("Status", "Stop Intake");
            } else {
                telemetry.addData("Status", "Go Intake");
            }

            telemetry.addData("Distance (cm)", distance);
            telemetry.update();
        }
    }
}